# RailwaySystem-Backend

ClodRail 后端聚合工程,采用 Spring Boot 3 + Spring Cloud + Spring Cloud Alibaba 的微服务架构。

> 📖 **项目完整介绍、架构图、亮点专题、快速启动等内容请看仓库根 [README.md](../README.md) 与 [Docs/](../Docs/README.md)。本文件只聚焦后端模块划分。**

## 模块结构

```text
RailwaySystem-Backend/
├── rs-gateway/              # 统一网关(WebFlux + JWT + RBAC)
├── rs-service/              # 5 个业务微服务
│   ├── rs-user/             # 用户、认证、RBAC、乘车人
│   ├── rs-ticket/           # 车站、线路、车次、余票、座位
│   ├── rs-order/            # 下单、支付、退票、TCC 分布式事务
│   ├── rs-mall/             # 积分商城、ES 检索
│   └── rs-assistant/        # AI 客服、Netty WebSocket
├── rs-api/                  # Feign 客户端 + 跨服务 DTO
├── rs-util/                 # 公共 starter
│   ├── rs-common/           # 统一响应、异常、工具
│   ├── rs-mysql/            # MyBatis Plus、分页、MetaObject
│   ├── rs-redis/            # RedisTemplate、分布式锁、Lua
│   ├── rs-rabbitmq/         # 死信、延迟、幂等封装
│   ├── rs-es/               # Elasticsearch 封装
│   ├── rs-seata/            # Seata TCC 默认装配
│   ├── rs-netty/            # Netty 启动装配
│   ├── rs-knife4j/          # 聚合文档
│   ├── rs-langchain4j/      # LangChain4j AI starter
│   ├── rs-thirdparty/       # 支付宝、OSS 等
│   ├── rs-xxl-job/          # XXL-Job starter
│   ├── rs-canal/            # Canal(预留)
│   └── rs-service-dependence/  # 业务服务统一依赖 pom
└── rs-web/                  # web 层公共
```

## 服务端口与注册名

| 服务模块 | Application Name | 端口 |
|---------|------------------|------|
| rs-gateway | rs-gateway | **18080** |
| rs-user | user-service | 18081 |
| rs-ticket | ticket-service | 18082 |
| rs-order | order-service | 18083 |
| rs-assistant(HTTP) | assistant-service | 18084 |
| rs-assistant(Netty WS) | — | 18085 |
| rs-mall | mall-service | 18086 |

## 构建

```bash
cd RailwaySystem-Backend
mvn clean install -DskipTests
```

## 相关文档

- 根 [README.md](../README.md) — 项目总览、架构、亮点
- [Docs/00-项目概述/架构总览.md](../Docs/00-项目概述/架构总览.md) — 完整分层与服务交互
- [Docs/00-项目概述/技术选型.md](../Docs/00-项目概述/技术选型.md) — 每个中间件的"为什么选它"
- [Docs/](../Docs/README.md) — 每个业务模块的架构图、时序图、核心代码解说
- [Docs/07-亮点技术专题/](../Docs/07-亮点技术专题/) — 秒杀一致性 / 网关鉴权 / AI 客服深度文章
- [Docs/99-部署运维/快速启动.md](../Docs/99-部署运维/快速启动.md) — 从零跑起来

## 许可

[MIT](../LICENSE)
