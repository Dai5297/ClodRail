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

### 4.2 启动后端

在 `RailwaySystem-Backend/` 下编译并按顺序启动：

- `rs-gateway`
- `rs-service/rs-user`
- `rs-service/rs-ticket`
- `rs-service/rs-order`
- `rs-service/rs-mall`
- `rs-service/rs-assistant`

### 4.3 启动前端

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
