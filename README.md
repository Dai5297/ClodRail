<div align="center">
  <img src="RailwaySystem-Frontend/rs-admin-web/public/logo-light.png" alt="ClodRail Logo" width="120" />

# 🚄 ClodRail - 仿 12306 铁路微服务系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D.svg)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

**一个面向学习与实战的前后端分离 + Spring Cloud 微服务项目**

覆盖用户、票务、订单、积分商城与 AI 客服等核心业务场景

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [项目文档](#-项目文档) • [技术架构](#-技术架构) • [贡献指南](CONTRIBUTING.md)

**个人博客**：[https://daiai.top/](https://daiai.top/)

</div>

---

## 📖 项目简介

ClodRail 是一个高仿 **12306 铁路购票系统** 的全栈微服务项目，涵盖从用户登录、车票查询、下单支付、订单状态流转、积分商城到 AI 客服实时会话等完整业务链路。

### 适合人群

- ✅ 想要学习 **微服务架构** 的开发者
- ✅ 准备 **求职面试** 需要项目经验的同学
- ✅ 希望了解 **分布式系统** 实践的工程师
- ✅ 对 **AI 技术集成** 感兴趣的开发者

---

## ✨ 功能特性

### 核心业务模块

| 模块 | 功能描述 |
|------|----------|
| 🧑‍💼 **用户服务** | 用户注册、登录、JWT 认证、RBAC 权限管理 |
| 🎫 **车票服务** | 车站/线路/车次管理、余票查询、座位分配 |
| 📦 **订单服务** | 订单创建、支付集成、退票、状态流转 |
| 🛒 **积分商城** | 积分获取/消费、商品兑换、ES 全文检索 |
| 🤖 **AI 客服** | 智能问答、WebSocket 实时通信、人工客服转接 |
| 🚪 **统一网关** | 路由转发、鉴权、限流、跨域处理 |

### 技术亮点

- 🔐 **安全认证**：JWT + Redis 会话 + Spring Security RBAC
- 🔥 **高并发处理**：Redis + Lua 原子扣减、分布式锁
- 📨 **异步解耦**：RabbitMQ 消息驱动架构
- 🔄 **分布式事务**：Seata TCC/AT 模式保证数据一致性
- 🚀 **实时通信**：Netty WebSocket 客服会话
- 🔍 **智能搜索**：Elasticsearch 商品全文检索
- 🤖 **AI 能力**：LangChain4j 大模型集成
- 📊 **接口文档**：Knife4j 自动生成 API 文档

---

## 🏗️ 技术架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端层                                   │
│     用户端 (Vue3 + Element Plus)    管理端 (Vue3 + Element Plus) │
└─────────────────────────────────────────────────────────────────┘
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│                    统一网关 (Spring Cloud Gateway)               │
│              路由转发 │ JWT 鉴权 │ 限流 │ 跨域处理                │
└─────────────────────────────────────────────────────────────────┘
                                ↓
┌──────────┬──────────┬──────────┬──────────┬──────────┐
│ 用户服务  │ 车票服务  │ 订单服务  │ 商城服务  │ 客服服务  │
│ :18081   │ :18082   │ :18083   │ :18086   │ :18084   │
└──────────┴──────────┴──────────┴──────────┴──────────┘
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│                        基础设施层                                │
│   Nacos │ MySQL │ Redis │ RabbitMQ │ Seata │ Elasticsearch      │
└─────────────────────────────────────────────────────────────────┘
```

### 技术栈

| 分类 | 技术 |
|------|------|
| **后端框架** | Spring Boot 3.5.5, Spring Cloud 2025.0.0, Spring Cloud Alibaba |
| **微服务** | Nacos, Gateway, OpenFeign, LoadBalancer |
| **数据存储** | MySQL 8+, Redis 7+, Elasticsearch 8.x |
| **中间件** | RabbitMQ, Seata, Netty |
| **前端** | Vue 3, Vite, Element Plus, TailwindCSS |
| **工具** | Knife4j, LangChain4j, XXL-JOB |

---

## 📁 项目结构

```
ClodRail/
├── RailwaySystem-Backend/      # 后端微服务
│   ├── rs-gateway/             # 统一网关 (18080)
│   ├── rs-service/             # 业务服务
│   │   ├── rs-user/            # 用户服务 (18081)
│   │   ├── rs-ticket/          # 车票服务 (18082)
│   │   ├── rs-order/           # 订单服务 (18083)
│   │   ├── rs-assistant/       # 客服服务 (18084)
│   │   └── rs-mall/            # 商城服务 (18086)
│   ├── rs-api/                 # Feign 接口 & DTO
│   └── rs-util/                # 公共工具模块
│
├── RailwaySystem-Frontend/     # 前端应用
│   ├── rs-user-web/            # 用户端 Web
│   └── rs-admin-web/           # 管理端 Web
│
├── RailwaySystem-DB/           # 数据库脚本
├── Docs/                       # 项目文档
└── 原型图/                      # UI 原型设计
```

---

## 🚀 快速开始

### 环境要求

| 软件 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 7.0+ |
| RabbitMQ | 3.12+ |
| Nacos | 2.2+ |

### 配置说明

项目使用示例配置文件，避免敏感信息泄露：

1. 复制示例配置文件
   ```bash
   # 在各服务目录下
   cp bootstrap-dev.example.yaml bootstrap-dev.yaml
   ```

2. 修改配置中的占位符
   - `<MYSQL_PASSWORD>` → 你的 MySQL 密码
   - `<REDIS_PASSWORD>` → 你的 Redis 密码
   - `<NACOS_NAMESPACE_ID>` → Nacos 命名空间 ID
   - 其他敏感配置...

### 启动步骤

#### 1. 启动基础设施
```bash
# Nacos
./nacos/bin/startup.sh -m standalone

# MySQL、Redis、RabbitMQ
# 使用 Docker 或本地安装
```

#### 2. 启动后端服务
```bash
cd RailwaySystem-Backend
mvn clean install -DskipTests

# 按顺序启动
# rs-gateway → rs-user → rs-ticket → rs-order → rs-mall → rs-assistant
```

#### 3. 启动前端
```bash
# 用户端
cd RailwaySystem-Frontend/rs-user-web
npm install && npm run dev

# 管理端
cd RailwaySystem-Frontend/rs-admin-web
npm install && npm run dev
```

#### 4. 访问系统
- 用户端：`http://localhost:5173`
- 管理端：`http://localhost:5174`
- API 文档：`http://localhost:18080/doc.html`
- Nacos：`http://localhost:8848/nacos`

---

## 📚 项目文档

| 文档 | 说明 |
|------|------|
| [项目介绍](Docs/00-项目概述/项目介绍.md) | 系统架构、核心流程、亮点功能 |
| [技术架构](Docs/00-项目概述/技术架构.md) | 架构设计、数据模型、部署方案 |
| [需求文档](Docs/00-项目概述/需求文档.md) | 功能需求、页面设计、用户流程 |
| [快速启动](Docs/99-部署运维/快速启动.md) | 本地环境搭建与启动指南 |
| [完整文档目录](Docs/README.md) | 按模块组织的完整文档索引 |

---

## 🤝 参与贡献

我们欢迎任何形式的贡献！

- 🐛 [报告 Bug](https://github.com/Dai5297/ClodRail/issues/new?template=bug_report.md)
- 💡 [提交功能建议](https://github.com/Dai5297/ClodRail/issues/new?template=feature_request.md)
- 📝 改进文档
- 🔧 提交代码

请阅读 [贡献指南](CONTRIBUTING.md) 了解详情。

---

## 📜 开源协议

本项目采用 [MIT](LICENSE) 开源协议。

---

## 🌟 支持项目

如果这个项目对你有帮助，请给一个 ⭐️ Star 支持一下！

---

<div align="center">

**⚡ 从零开始，构建企业级微服务系统！**

Made with ❤️ by [Dai5297](https://github.com/Dai5297)

</div>
