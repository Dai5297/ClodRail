# 更新日志

本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/) 规范。

## [Unreleased]

### 新增
- 待添加

### 变更
- 待添加

### 修复
- 待添加

---

## [1.0.0] - 2024-12-30

### 新增

#### 核心功能
- ✨ 用户服务：注册、登录、JWT 认证、个人信息管理
- ✨ 车票服务：车站管理、线路管理、车次查询、余票查询
- ✨ 订单服务：订单创建、支付集成、退票、订单状态流转
- ✨ 积分商城：商品展示、积分兑换、ES 全文检索
- ✨ AI 客服助手：基于 LangChain4j 的智能问答、WebSocket 实时通信

#### 技术特性
- 🏗️ Spring Cloud 微服务架构（Gateway + Nacos + OpenFeign）
- 🔐 JWT + Spring Security 统一认证鉴权
- 💾 Redis 缓存 + 分布式锁
- 📨 RabbitMQ 消息队列异步处理
- 🔄 Seata 分布式事务
- 🔍 Elasticsearch 商品搜索
- 📡 Netty WebSocket 实时通信
- 📖 Knife4j API 文档

#### 前端应用
- 🖥️ 用户端 Web 应用（Vue 3 + Element Plus）
- 🛠️ 管理端 Web 应用（Vue 3 + Element Plus）

### 文档
- 📚 项目整体文档
- 📚 技术架构文档
- 📚 API 接口文档
- 📚 数据库设计文档
- 📚 部署指南

---

## 版本说明

- `[Unreleased]` - 开发中的功能
- `[x.y.z]` - 已发布版本
  - `x` - 主版本号：不兼容的 API 变更
  - `y` - 次版本号：向下兼容的功能新增
  - `z` - 修订号：向下兼容的问题修复
