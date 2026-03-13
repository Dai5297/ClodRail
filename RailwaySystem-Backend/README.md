<div align="center">

# 🚄 仿 12306 铁路微服务系统 - 后端

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**一个适合新手入门的企业级微服务实战项目**

[功能特性](#-功能特性) • [技术架构](#-技术架构) • [快速开始](#-快速开始) • [项目结构](#-项目结构) • [学习路线](#-学习路线)

</div>

---

## 📖 项目简介

本项目是一个高仿 **12306 铁路购票系统** 的微服务实战项目，涵盖了用户管理、车票查询、订单处理、积分商城、AI 客服助手等完整的业务场景。项目采用主流的 **Spring Cloud 微服务架构**，整合了当下热门的技术栈，非常适合作为：

- ✅ **微服务架构学习**：从零搭建完整微服务体系
- ✅ **Spring Cloud 实战**：掌握 Nacos、Gateway、OpenFeign 等组件
- ✅ **分布式系统实践**：学习分布式事务、缓存、消息队列等解决方案
- ✅ **AI 技术集成**：体验 LangChain4j 在实际项目中的应用
- ✅ **求职项目经验**：可直接作为简历亮点项目

---

## ✨ 功能特性

### 核心业务模块

| 模块               | 功能描述                               |
| ------------------ | -------------------------------------- |
| 🧑‍💼 **用户服务**    | 用户注册、登录、个人信息管理、JWT 认证 |
| 🎫 **车票服务**    | 列车查询、车站管理、线路管理、余票查询 |
| 📦 **订单服务**    | 订单创建、支付、退票、订单查询         |
| 🛒 **积分商城**    | 积分商品浏览、兑换、ES 搜索            |
| 🤖 **AI 客服助手** | 基于 LangChain4j 的智能客服问答系统    |
| 🚪 **统一网关**    | 路由转发、鉴权、限流、跨域处理         |

### 技术亮点

- 🔐 **安全认证**：JWT + Spring Security 实现无状态认证
- 🔥 **高并发处理**：Redis 缓存 + 分布式锁优化热点数据
- 📨 **异步解耦**：RabbitMQ 实现订单异步处理
- 🔄 **分布式事务**：Seata 保证跨服务数据一致性
- 🚀 **高性能通信**：Netty 实现 WebSocket 实时消息推送
- 🔍 **智能搜索**：ElasticSearch 实现商品全文检索
- 📊 **接口文档**：Knife4j 自动生成交互式 API 文档
- 🤖 **AI 能力**：LangChain4j 集成大语言模型

---

## 🏗️ 技术架构

### 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层                                │
│          (Vue3 + Element Plus + Axios)                     │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   统一网关 (Gateway)                         │
│        路由转发 | 鉴权 | 限流 | 跨域处理                     │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌────────────┬──────────────┬──────────────┬─────────────────┐
│  用户服务   │   车票服务    │   订单服务    │   积分商城      │
│ rs-user    │  rs-ticket   │  rs-order    │   rs-mall       │
└────────────┴──────────────┴──────────────┴─────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      基础设施层                              │
│  Nacos | Redis | MySQL | RabbitMQ | Seata | ElasticSearch │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈清单

#### 核心框架

- **Spring Boot 3.5.5**：基础开发框架
- **Spring Cloud 2025.0.0**：微服务全家桶
- **Spring Cloud Alibaba 2023.0.3.2**：阿里巴巴微服务组件

#### 微服务组件

- **Nacos**：服务注册与配置中心
- **Gateway**：统一 API 网关
- **OpenFeign**：声明式服务调用
- **LoadBalancer**：客户端负载均衡

#### 数据存储

- **MySQL 8.0+**：关系型数据库
- **Redis 7.0+**：分布式缓存
- **ElasticSearch 8.x**：搜索引擎

#### 中间件

- **RabbitMQ**：消息队列
- **Seata**：分布式事务解决方案
- **Netty**：高性能网络通信

#### 工具库

- **Mybatis Plus**：数据持久层增强
- **Hutool**：Java 工具类库
- **Lombok**：简化 Java 代码
- **Knife4j**：API 文档生成
- **LangChain4j**：大模型集成框架

---

## 📁 项目结构

```
RailwaySystem-Backend/
├── rs-gateway/                 # 统一网关服务
│   ├── filters/               # 网关过滤器（鉴权、日志等）
│   └── properties/            # 网关配置属性
│
├── rs-service/                # 业务服务模块
│   ├── rs-user/              # 用户服务
│   ├── rs-ticket/            # 车票服务
│   ├── rs-order/             # 订单服务
│   ├── rs-mall/              # 积分商城服务
│   └── rs-assistant/         # AI客服助手服务
│
├── rs-api/                    # 接口定义模块
│   ├── client/               # Feign 客户端接口
│   └── dto/                  # 数据传输对象
│
├── rs-util/                   # 工具模块
│   ├── rs-common/            # 通用工具类（异常、响应、工具类）
│   ├── rs-mysql/             # MySQL 配置与拦截器
│   ├── rs-redis/             # Redis 配置与工具类
│   ├── rs-rabbitmq/          # RabbitMQ 配置
│   ├── rs-es/                # ElasticSearch 配置
│   ├── rs-seata/             # Seata 分布式事务配置
│   ├── rs-netty/             # Netty 配置
│   ├── rs-knife4j/           # Knife4j 文档配置
│   ├── rs-langchain4j/       # LangChain4j AI配置
│   ├── rs-thirdparty/        # 第三方服务（OSS等）
│   └── rs-service-dependence/# 服务公共依赖
│
└── pom.xml                    # 父级 Maven 配置
```

### 模块说明

| 模块           | 说明     | 核心技术                       |
| -------------- | -------- | ------------------------------ |
| `rs-gateway`   | 统一网关 | Spring Cloud Gateway, JWT 鉴权 |
| `rs-user`      | 用户管理 | Spring Security, JWT, JPA      |
| `rs-ticket`    | 车票管理 | Redis 缓存, Mybatis Plus       |
| `rs-order`     | 订单处理 | Seata 分布式事务, RabbitMQ     |
| `rs-mall`      | 积分商城 | ElasticSearch, 分页查询        |
| `rs-assistant` | AI 客服  | LangChain4j, Netty WebSocket   |

---

## 🚀 快速开始

### 环境要求

| 软件          | 版本要求   |
| ------------- | ---------- |
| JDK           | 17+        |
| Maven         | 3.8+       |
| MySQL         | 8.0+       |
| Redis         | 7.0+       |
| RabbitMQ      | 3.12+      |
| Nacos         | 2.2+       |
| ElasticSearch | 8.x (可选) |

### 启动步骤

#### 1️⃣ 启动基础设施

```bash
# 启动 Nacos (默认端口 8848)
cd nacos/bin
./startup.sh -m standalone

# 创建 Nacos 数据库并导入共享配置
mysql -u root -p -e "CREATE DATABASE nacos DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p nacos < ../docs/99-部署运维/nacos.sql

# 启动 Redis
redis-server

# 启动 RabbitMQ
rabbitmq-server
```

#### 2️⃣ 配置 Nacos

访问 Nacos 控制台：`http://localhost:8848/nacos`（用户名/密码：nacos/nacos）

导入配置文件：

- 在 Nacos 中创建或确认命名空间
- 导入仓库根目录下的 `docs/99-部署运维/nacos.sql`
- 按本地环境修改数据库、Redis、RabbitMQ、支付和 AI 相关配置

#### 3️⃣ 编译项目

```bash
cd RailwaySystem-Backend
mvn clean install -DskipTests
```

#### 4️⃣ 启动服务

**推荐启动顺序**：

```bash
# 1. 启动网关
cd rs-gateway
mvn spring-boot:run

# 2. 启动用户服务
cd rs-service/rs-user
mvn spring-boot:run

# 3. 启动车票服务
cd rs-service/rs-ticket
mvn spring-boot:run

# 4. 启动订单服务
cd rs-service/rs-order
mvn spring-boot:run

# 5. 启动积分商城服务
cd rs-service/rs-mall
mvn spring-boot:run

# 6. 启动AI客服助手
cd rs-service/rs-assistant
mvn spring-boot:run
```

#### 5️⃣ 验证服务

- **Nacos 控制台**：`http://localhost:8848/nacos` - 检查所有服务是否注册成功
- **Gateway 健康检查**：`http://localhost:8080/actuator/health`
- **接口文档**：`http://localhost:8080/doc.html` - Knife4j 聚合文档

---

## 📚 学习路线

### 🎯 新手入门（1-2 周）

1. **理解项目架构**

   - 学习微服务基本概念
   - 了解项目模块划分
   - 阅读项目接口文档

2. **环境搭建实践**

   - 按照快速开始指南搭建环境
   - 成功启动所有服务
   - 调试接口验证功能

3. **阅读核心代码**
   - 从 `rs-user` 用户服务开始
   - 学习 Controller -> Service -> Mapper 三层架构
   - 理解 JWT 认证流程

### 🚀 进阶提升（2-4 周）

4. **微服务组件深入**

   - Nacos 服务注册与配置管理
   - Gateway 网关路由与过滤器
   - OpenFeign 服务间调用

5. **分布式技术实践**

   - Redis 缓存策略与分布式锁
   - RabbitMQ 消息队列应用
   - Seata 分布式事务处理

6. **高级特性探索**
   - ElasticSearch 全文检索
   - Netty WebSocket 实时通信
   - LangChain4j AI 集成

### 💡 实战拓展（持续学习）

7. **性能优化**

   - 接口性能监控与优化
   - 数据库查询优化
   - 缓存穿透/雪崩解决方案

8. **功能扩展**
   - 添加新的业务模块
   - 集成新的技术组件
   - 开发自定义功能

---

## 📖 核心知识点

### 微服务架构

- [x] 服务拆分原则
- [x] 服务注册与发现
- [x] 配置中心管理
- [x] API 网关设计
- [x] 服务间通信

### 分布式技术

- [x] 分布式事务（Seata AT 模式）
- [x] 分布式锁（Redis）
- [x] 分布式缓存（Redis）
- [x] 消息队列（RabbitMQ）

### 数据库设计

- [x] 多表关联设计
- [x] 索引优化
- [x] 分页查询
- [x] JPA/Mybatis Plus 使用

### 安全与认证

- [x] JWT 无状态认证
- [x] Spring Security 集成
- [x] 网关鉴权过滤器
- [x] 密码加密存储

### 搜索引擎

- [x] ElasticSearch 索引设计
- [x] 分词与全文检索
- [x] 复合查询与聚合

### AI 技术

- [x] LangChain4j 集成
- [x] 大模型 API 调用
- [x] 智能问答系统

---

## 🛠️ 开发工具推荐

- **IDE**：IntelliJ IDEA（推荐旗舰版）
- **API 测试**：Postman / Apifox
- **数据库工具**：Navicat / DataGrip
- **Redis 工具**：RedisInsight / AnotherRedisDesktopManager
- **Git 工具**：Git CLI / SourceTree

---

## 📝 接口文档

项目集成了 **Knife4j**，启动服务后访问：

- **聚合文档**：`http://localhost:18080/doc.html`
- **用户服务**：`http://localhost:18001/doc.html`
- **车票服务**：`http://localhost:18002/doc.html`
- **订单服务**：`http://localhost:18003/doc.html`
- **积分商城**：`http://localhost:18004/doc.html`

详细接口文档见 `docs/` 目录。

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📧 联系方式

- **项目交流群**：[待创建]
- **问题反馈**：提交 Issue

---

## 📜 开源协议

本项目采用 [MIT](LICENSE) 开源协议。

---

## 🌟 Star History

如果这个项目对你有帮助，请给一个 ⭐️ Star 支持一下！

[![Star History Chart](https://api.star-history.com/svg?repos=yourname/RailwaySystem&type=Date)](https://star-history.com/#yourname/RailwaySystem&Date)

---

<div align="center">

**⚡ 从零开始，构建企业级微服务系统！**

Made with ❤️ by Railway System Team

</div>
