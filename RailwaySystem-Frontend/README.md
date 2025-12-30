# 🖥️ ClodRail 前端项目

本目录包含 ClodRail 项目的前端应用。

## 📁 目录结构

```
RailwaySystem-Frontend/
├── rs-user-web/        # 用户端 Web 应用
├── rs-admin-web/       # 管理端 Web 应用（后台管理系统）
└── README.md           # 本文件
```

## 🚀 快速开始

### 用户端

```bash
cd rs-user-web
npm install
npm run dev
```

访问 `http://localhost:5173`

### 管理端

```bash
cd rs-admin-web
npm install
npm run dev
```

访问 `http://localhost:5174`

## 🛠️ 技术栈

### 用户端 (rs-user-web)
- Vue 3 + Vite
- Element Plus + Ant Design Vue
- TailwindCSS v3
- Axios + Vue Router

### 管理端 (rs-admin-web)
- Vue 3 + Vite
- Element Plus
- TailwindCSS v4
- ECharts 数据可视化
- Axios + Vue Router + i18n

---

## 📱 用户端功能 (rs-user-web)

面向乘客的购票系统，主要功能：

- **用户认证**：注册、登录、个人信息管理
- **车票查询**：按出发站/到达站/日期查询车票
- **在线购票**：选座、下单、支付宝支付
- **订单管理**：查看订单、退票、改签
- **乘车人管理**：添加/编辑常用乘车人
- **积分商城**：积分查询、商品兑换
- **AI 客服**：智能问答、人工客服转接

---

## 🔧 后台管理系统 (rs-admin-web)

面向管理员的运营管理系统，主要功能模块：

### 用户管理
- 用户列表查询与管理
- 用户状态管理（启用/禁用）
- 管理员账号管理
- 角色权限配置

### 车票管理
- **车站管理**：车站信息 CRUD、车站状态管理
- **线路管理**：线路创建、途经站配置
- **车次管理**：列车信息、运行时刻表
- **车票管理**：票价设置、余票监控

### 订单管理
- 订单列表查询
- 订单详情查看
- 退票/改签审核
- 订单数据统计

### 商城管理
- 商品上下架管理
- 商品信息编辑
- 库存管理
- 兑换订单处理

### 客服管理
- 会话记录查看
- AI 对话记忆管理
- 人工客服工作台

### 数据统计
- 销售数据看板
- 用户增长趋势
- 热门线路统计
- 收入报表

---

## ⚙️ 配置说明

### API 代理

开发环境下，前端通过 Vite 代理转发 API 请求：

| 路径 | 目标 | 说明 |
|------|------|------|
| `/api` | `http://localhost:18080` | 网关 API |
| `/ws` | `http://localhost:18085` | WebSocket |

详见各项目的 `vite.config.js`。

## 📝 注意事项

1. 确保后端服务已启动后再运行前端
2. 如遇到跨域问题，检查网关 CORS 配置
3. WebSocket 连接需要客服服务 (rs-assistant) 运行

## 📚 相关文档

- 项目文档：[/Docs](/Docs)
- UI 原型图：[/原型图](/原型图)
- 技术架构：[/Docs/00-项目概述/技术架构.md](/Docs/00-项目概述/技术架构.md)
