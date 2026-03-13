# ClodRail

一个面向学习与实战的仿 12306 铁路微服务项目，覆盖用户、票务、订单、积分商城和 AI 客服等核心业务场景。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D.svg)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 项目定位

ClodRail 不是一个单纯的演示页面，而是一套完整的前后端分离微服务练习项目，适合用来了解：

- 微服务拆分方式与服务协作
- 票务场景中的库存、并发与事务处理
- Gateway、Nacos、Redis、RabbitMQ、Seata 等中间件在真实业务中的组合方式
- Vue 3 用户端与管理端的协作开发方式
- AI 客服在业务系统中的接入方式

## 你可以先看什么

如果你是第一次打开这个仓库，推荐按下面顺序阅读：

1. [项目介绍](docs/00-项目概述/项目介绍.md)
2. [技术架构](docs/00-项目概述/技术架构.md)
3. [快速启动](docs/99-部署运维/快速启动.md)
4. [完整文档导航](docs/README.md)

如果你只想快速判断项目是否值得继续看：

- 看后端结构：[RailwaySystem-Backend/README.md](RailwaySystem-Backend/README.md)
- 看前端结构：[RailwaySystem-Frontend/README.md](RailwaySystem-Frontend/README.md)
- 看功能范围：[docs/00-项目概述/需求文档.md](docs/00-项目概述/需求文档.md)

## 当前功能状态

项目适合开源展示，但目前更准确的定位是“持续迭代中的学习型项目”。建议用下面的状态理解仓库内容：

| 模块 | 状态 | 说明 |
|------|------|------|
| 用户服务 | 已实现 | 注册、登录、认证、RBAC、乘车人管理 |
| 车票服务 | 已实现 | 车站、线路、车次、余票、座位库存 |
| 订单服务 | 已实现 | 下单、支付、退票、订单流转 |
| 积分商城 | 已实现 | 商品展示、积分兑换、搜索 |
| AI 客服 | 已实现 | 智能问答、WebSocket 实时通信、会话管理 |
| VIP 体系 | 开发中 | 需求文档中已标记“待完成” |
| 部署脚本 | 基础可用 | 本地启动文档已提供，容器化和一键化仍可继续完善 |

这类状态说明很重要，它能帮助开源读者快速判断“哪些是现成可跑的，哪些是规划中的”，减少误解。

## 技术栈

| 分类 | 技术 |
|------|------|
| 后端 | Spring Boot 3.5.5、Spring Cloud 2025.0.0、Spring Cloud Alibaba |
| 前端 | Vue 3、Vite、Element Plus、TailwindCSS |
| 存储 | MySQL 8+、Redis 7+、Elasticsearch 8.x |
| 中间件 | RabbitMQ、Seata、Netty、Nacos |
| 其他 | Knife4j、LangChain4j、XXL-JOB |

## 仓库结构

```text
RailwaySystem/
├── RailwaySystem-Backend/     # 后端微服务
├── RailwaySystem-Frontend/    # 用户端 + 管理端前端
├── RailwaySystem-DB/          # 数据库相关资源
├── docs/                      # 项目文档总目录
├── 原型图/                     # 原型与接口分析资料
├── CONTRIBUTING.md            # 贡献指南
├── CODE_OF_CONDUCT.md         # 社区行为准则
├── SECURITY.md                # 安全说明
└── CHANGELOG.md               # 版本更新记录
```

## 最小启动路径

如果你只是想在本地把项目跑起来，可以先按最小路径操作：

1. 准备 `MySQL`、`Redis`、`RabbitMQ`、`Nacos`
2. 导入 Nacos 配置脚本：`docs/99-部署运维/nacos.sql`
3. 编译并启动后端服务
4. 启动 `rs-user-web` 或 `rs-admin-web`
5. 通过网关访问接口文档和前端页面

完整步骤见 [快速启动](docs/99-部署运维/快速启动.md)。

## 文档导航

| 场景 | 文档 |
|------|------|
| 我想快速了解项目 | [项目介绍](docs/00-项目概述/项目介绍.md) |
| 我想看系统拆分和技术选型 | [技术架构](docs/00-项目概述/技术架构.md) |
| 我想看模块文档总目录 | [docs 首页](docs/README.md) |
| 我想本地运行项目 | [快速启动](docs/99-部署运维/快速启动.md) |
| 我想参与贡献 | [贡献指南](CONTRIBUTING.md) |

## 开源前你需要知道

- 仓库中已经提供 `LICENSE`、`CONTRIBUTING.md`、`CODE_OF_CONDUCT.md`、`SECURITY.md`，开源基础信息是齐的。
- 文档中默认以本地开发为主，生产级部署、压测数据、演示环境说明还可以继续补充。
- 如果你准备正式公开发布，建议再补一份系统截图、演示账号说明和 roadmap。

## 贡献

欢迎提交 Issue、文档修订和代码 PR。开始之前建议先阅读 [贡献指南](CONTRIBUTING.md)。
