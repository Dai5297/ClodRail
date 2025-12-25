<div align="center">
  <img src="RailwaySystem-Frontend/rs-admin-web/public/logo-light.png" alt="ClodRail Logo" width="120" />

# 🚄 ClodRail（仿 12306 铁路微服务系统）

个人博客：<https://daiai.top/>

一个面向学习与实战的 **前后端分离 + Spring Cloud 微服务** 项目，覆盖用户、票务、订单、积分商城与 AI 客服等核心业务场景。

</div>

---

## 1. 项目概览

本仓库包含：

- **后端**：`RailwaySystem-Backend/`（Spring Boot + Spring Cloud）
- **前端**：`RailwaySystem-Frontend/`
  - `rs-user-web`：用户端（购票/订单/积分商城/客服）
  - `rs-admin-web`：管理端（车站/线路/车次/车票/订单/权限/客服）
- **项目文档**：`Docs/`

推荐先阅读：

- `Docs/ClodRail项目介绍.md`
- `Docs/项目整体文档.md`

---

## 2. 技术栈

### 2.1 后端

- Spring Boot 3.5.5 / Spring Cloud 2025.0.0 / Spring Cloud Alibaba 2023.0.3.2
- Nacos（注册中心 + 配置中心）
- Gateway（统一入口、路由、鉴权）
- OpenFeign（服务调用）
- MySQL / Redis / RabbitMQ / Seata / Elasticsearch
- Netty WebSocket（客服实时通信）
- Knife4j（接口文档）
- XXL-JOB（任务调度）
- LangChain4j（大模型集成）

### 2.2 前端

- Vue 3 + Vite
- Element Plus /（用户端还集成 Ant Design Vue）
- Axios + Vue Router
- TailwindCSS

---

## 3. 后端服务与端口

| 模块     | 服务名            | 端口  | 说明                                                 |
| -------- | ----------------- | ----- | ---------------------------------------------------- |
| 网关     | rs-gateway        | 18080 | 统一入口（WebFlux）                                  |
| 用户服务 | user-service      | 18081 | 用户/认证/权限                                       |
| 票务服务 | ticket-service    | 18082 | 车票/座位/线路/车站                                  |
| 订单服务 | order-service     | 18083 | 下单/支付/订单流转                                   |
| 客服服务 | assistant-service | 18084 | AI/客服 HTTP API                                     |
| Netty WS | -                 | 18085 | WebSocket（/ws/assistant/user、/ws/assistant/agent） |
| 商城服务 | mall-service      | 18086 | 积分/商城/检索                                       |

---

## 4. 本地启动（简要）

### 4.1 准备基础设施

- Nacos：默认 8848
- MySQL / Redis / RabbitMQ
- 可选：Elasticsearch、Seata Server、XXL-JOB Admin

> 说明：本项目通过 Nacos 引入 `shared-*.yaml` 共享配置；请先在 Nacos 中准备这些配置项。

### 4.2 配置方式（示例配置已提供）

为了避免敏感信息（密码/Key）被提交到仓库：

- 仓库中提供的是 **示例配置**（可提交）：`bootstrap-dev.example.yaml`、`Docs/nacos-config-example/shared-*.yaml`
- 你本地实际生效的配置应为：`bootstrap-dev.yaml`（已被 `.gitignore` 忽略，不会被提交）

#### 方式 A：按服务复制本地配置（推荐新手）

在每个后端服务目录下执行：

- 将 `src/main/resources/bootstrap-dev.example.yaml` 复制为 `src/main/resources/bootstrap-dev.yaml`
- 将占位符替换为你的真实值，例如：
  - `<MYSQL_PASSWORD>`、`<REDIS_PASSWORD>`
  - `<RABBITMQ_USERNAME>`、`<RABBITMQ_PASSWORD>`
  - `<ALIPAY_*>`、`<AI_API_KEY>`
  - `<NACOS_NAMESPACE_ID>`

示例文件位置：

- `RailwaySystem-Backend/rs-gateway/src/main/resources/bootstrap-dev.example.yaml`
- `RailwaySystem-Backend/rs-service/rs-user/src/main/resources/bootstrap-dev.example.yaml`
- `RailwaySystem-Backend/rs-service/rs-ticket/src/main/resources/bootstrap-dev.example.yaml`
- `RailwaySystem-Backend/rs-service/rs-order/src/main/resources/bootstrap-dev.example.yaml`
- `RailwaySystem-Backend/rs-service/rs-mall/src/main/resources/bootstrap-dev.example.yaml`
- `RailwaySystem-Backend/rs-service/rs-assistant/src/main/resources/bootstrap-dev.example.yaml`

#### 方式 B：导入 Nacos 共享配置（适合多环境/团队协作）

1. 启动 Nacos：`http://localhost:8848/nacos`（默认 nacos/nacos）
2. 创建命名空间并记录 namespaceId
3. 将 `Docs/nacos-config-example/` 下的 `shared-*.yaml` 导入 Nacos
4. 各服务的 `bootstrap.yaml` 会通过 `spring.config.import` 引用这些共享配置

> 提示：如果你采用方式 B，建议把数据库/Redis/MQ 等连接信息放在 Nacos shared 配置中，本地 `bootstrap-dev.yaml` 只保留必要的“非敏感”差异化配置。

### 4.3 启动后端

在 `RailwaySystem-Backend/` 下编译并按顺序启动：

- `rs-gateway`
- `rs-service/rs-user`
- `rs-service/rs-ticket`
- `rs-service/rs-order`
- `rs-service/rs-mall`
- `rs-service/rs-assistant`

### 4.4 启动前端

- 用户端：`RailwaySystem-Frontend/rs-user-web`

  - `npm install`
  - `npm run dev`

- 管理端：`RailwaySystem-Frontend/rs-admin-web`
  - `npm install`
  - `npm run dev`

---

## 5. 文档与接口

- 聚合接口文档（Knife4j）：`http://localhost:18080/doc.html`
- 更完整的说明见：`Docs/项目整体文档.md`
