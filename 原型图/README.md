# 🎨 ClodRail UI 原型图

本目录包含 ClodRail 项目的 UI 原型设计，使用 HTML/CSS/JS 实现的可交互原型。

## 📁 目录结构

```
原型图/
├── README.md           # 本文件
├── admin-web/          # 管理端原型
│   ├── index.html      # 管理端首页
│   ├── login.html      # 管理员登录
│   ├── pages/          # 各功能页面
│   ├── css/            # 样式文件
│   └── js/             # 脚本文件
│
├── user-web/           # 用户端 Web 原型
│   ├── index.html      # 首页
│   ├── train-search.html    # 车次查询
│   ├── seat-selection.html  # 选座购票
│   ├── payment.html         # 支付页面
│   ├── profile.html         # 个人中心
│   ├── points-mall.html     # 积分商城
│   ├── ai-assistant.html    # AI 助手
│   ├── css/            # 样式文件
│   ├── js/             # 脚本文件
│   └── images/         # 图片资源
│
└── user-mobile/        # 用户端移动端原型
    ├── index.html      # 首页
    ├── search.html     # 搜索页
    ├── ticket.html     # 车票详情
    ├── order.html      # 订单页
    ├── profile.html    # 个人中心
    ├── css/            # 样式文件
    └── js/             # 脚本文件
```

## 🚀 如何查看

### 方式一：直接打开 HTML 文件
双击对应的 `index.html` 文件即可在浏览器中查看。

### 方式二：使用本地服务器（推荐）
```bash
# 进入原型图目录
cd 原型图/user-web

# 使用 Python 启动简单服务器
python -m http.server 8000

# 或使用 Node.js 的 live-server
npx live-server
```

然后访问 `http://localhost:8000`

## 📱 原型说明

### 管理端 (admin-web)
管理后台原型，包含：
- 登录页面
- 数据仪表盘
- 用户管理
- 车次管理
- 订单管理
- 积分商城管理
- 系统设置

### 用户端 Web (user-web)
用户端网页原型，包含：
- 首页（搜索、热门线路）
- 车次查询与筛选
- 选座购票流程
- 支付页面
- 个人中心
- 积分商城
- AI 智能助手

### 用户端移动端 (user-mobile)
移动端适配原型，包含：
- 首页
- 车票搜索
- 订单管理
- 个人中心

## 🎯 设计规范

### 配色方案
- 主色：`#1890FF` (蓝色)
- 成功：`#52C41A` (绿色)
- 警告：`#FAAD14` (橙色)
- 错误：`#FF4D4F` (红色)
- 背景：`#F5F5F5`
- 文字：`#333333`

### 字体
- 中文：微软雅黑、PingFang SC
- 英文：Arial、Helvetica

### 间距
- 基础间距：8px
- 卡片内边距：16px / 24px
- 页面边距：24px

## 📝 注意事项

1. 原型仅用于展示 UI 设计，不包含实际业务逻辑
2. `node_modules` 目录已被 `.gitignore` 忽略
3. 如需修改原型，请保持设计规范一致性
4. 原型图设计文档详见 [Docs/仿12306铁路系统页面原型图设计.md](../Docs/仿12306铁路系统页面原型图设计.md)
