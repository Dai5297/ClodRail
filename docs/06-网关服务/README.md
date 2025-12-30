# 网关服务 (rs-gateway)

网关服务是系统的统一入口，负责路由转发、鉴权和限流。

## 服务信息

- **服务名**：rs-gateway
- **端口**：18080
- **模块路径**：`RailwaySystem-Backend/rs-gateway`

## 功能职责

- 统一 API 入口
- 路由转发
- JWT 鉴权
- 权限校验
- 限流
- 跨域处理

## 文档列表

| 文档 | 说明 |
|------|------|
| [路由配置.md](路由配置.md) | 路由规则说明 |
| [鉴权机制.md](鉴权机制.md) | JWT 鉴权流程 |

## 技术要点

- Spring Cloud Gateway (WebFlux)
- JWT + Redis 会话
- RBAC 权限控制
