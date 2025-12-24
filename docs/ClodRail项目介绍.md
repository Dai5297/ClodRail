# 项目总览

[ClodRail](https://github.com/Dai5297/RailwaySystem-Backend)是一套仿 12306 的铁路票务系统，采用前后端分离与微服务架构，面向高并发购票与实时客服场景。读完本文，你将清晰了解：

- 系统整体架构与关键模块
- 一个订单从“搜索到支付”的端到端流转
- 热门车票的秒杀一致性保障策略
- 前后端技术栈与运行方式

## 整体架构

![架构图](./assets/架构图.png)

系统由两个前端应用与多个后端服务组成，统一通过网关对外提供访问：

- 前端应用
  - `rs-user-web`：用户端（搜索购票、订单支付、积分商城、AI 客服）
  - `rs-admin-web`：管理端（运营仪表盘、车次/线路/车票、订单、权限、客服回复）
- 后端服务
  - `rs-gateway`：统一入口，路由转发、鉴权、限流与跨域
  - `rs-service/rs-ticket`：车站、线路、座位与车票库存管理与查询
  - `rs-service/rs-order`：订单创建、支付、退改签与异步处理
  - `rs-service/rs-mall`：积分商城，商品展示与积分兑换
  - `rs-service/rs-assistant`：AI 客服助手，基于 Netty + WebSocket 的实时通信
  - `rs-service/rs-user`：用户管理与权限控制
  - `rs-api`：统一的数据传输对象与 Feign 客户端接口定义

核心中间件与数据存储：

- MySQL：业务数据持久化（用户、车票、订单、商品等）
- Redis：缓存、原子扣减与并发控制
- RabbitMQ：消息驱动的异步处理（订单落库、积分发放等）
- Elasticsearch：商品搜索与检索
- Seata：TCC 分布式事务，保证跨服务一致性
- Netty：高性能网络通信与 WebSocket 长连接

## 模块职责一览

- `rs-gateway`：统一入口，基于路径进行路由转发，集成 Nacos 做服务发现与配置管理
- `rs-service`：业务代码处理模块，包含订单、车票、积分商城、客服助手等核心业务逻辑
- `rs-util`：工具类模块，包含对引入的中间件（如 Redis、RabbitMQ、Elasticsearch）的封装与通用工具方法
- `rs-web`：静态页面渲染模块，负责渲染用户端与管理端的静态 HTML 页面
- `rs-api`：统一 DTO/Feign 接口，确保跨服务调用的一致性

## 亮点功能

### 高并发下单

![下单流程](./assets/下单流程.png)

1. 用户在 `rs-user-web` 搜索车次，网关将请求路由至 `rs-ticket` 查询车票与余票
2. 用户选择座位与乘客信息并发起下单，网关路由请求至 `rs-order`
3. `rs-order` 根据车票类型走不同流程：
   - 普通车票：直接检查余票 → 请求 `rs-ticket` 分配座位 → 写入订单与座位明细 → 发送“积分发放”消息
   - 热门车票（秒杀）：使用 Redis + Lua 进行余票的原子预扣与座位占用；暂存订单信息，待 TCC 提交阶段落库
4. 订单创建成功后，`rs-order` 通过 RabbitMQ 发送消息：
   - “订单落库”消息（热门票）：将暂存的订单信息写入 MySQL，并补齐座位明细
   - “积分发放”消息：调用 `rs-mall` 为用户增加积分
5. 用户前端展示订单结果并进入支付流程，后续状态流转由订单服务与消息驱动完成

#### 热门秒杀如何保持一致性

热门车票在高并发下的策略概览：

- Redis + Lua：余票扣减使用脚本保证原子性，避免并发下的库存错乱
- Seata TCC：订单创建采用两阶段事务
  - Try：预扣资源、暂存订单信息
  - Commit：确认成功后落库、派发消息
  - Rollback：异常则回滚余票、释放座位；删除暂存信息
- RabbitMQ：订单成功后的落库与积分发放采用消息异步处理，提升系统吞吐与解耦

### 实时通信与客服

![通信流程](./assets/通信流程.png)

- `rs-assistant` 通过 Netty + WebSocket 与用户建立长连接，支持消息推送与上下文会话管理
- 可与 AI 能力结合，对常见问题提供引导与问答，并支持转人工客服

### RBAC 权限管理

- `rs-user` 服务负责用户注册、登录与权限管理
- 基于 Spring Security 实现 RBAC（角色-基于权限的访问控制）
- 管理员可在 `rs-admin-web` 管理用户、角色与权限，为不同用户分配不同操作权限

## 前后端技术栈（精简版）

- 后端：Spring Boot、Spring Cloud、Gateway、OpenFeign、Nacos、Spring Security、MyBatis
- 中间件：MySQL、Redis、RabbitMQ、Elasticsearch、Seata、Netty
- 前端：Vue 3、Vite、Vue Router、Element Plus、Ant Design Vue、TailwindCSS


## 阅读路径建议

- 先浏览“整体架构”和“模块职责一览”，获得系统纵览
- 再看“一次购票的完整旅程”，理解服务协同的端到端流转
- 最后阅读“热门秒杀如何保持一致性”和“实时通信与客服”，掌握关键能力的实现思路
