# 铁路系统完整 API 接口文档

## 概述

本文档基于后端项目实际实现，详细描述了铁路系统的 API 接口设计。系统采用 RESTful API 设计风格，基于 Spring Boot 微服务架构，目前实现了用户认证和用户信息管理等核心功能。

## 通用规范

### 请求格式

- **Content-Type**: `application/json`
- **字符编码**: UTF-8
- **请求方法**: GET, POST, PUT, DELETE

### 响应格式

所有 API 响应均采用统一的 JSON 格式，使用 `RespResult<T>` 封装：

```json
{
  "code": 200,
  "message": null,
  "data": {}
}
```

### 状态码说明

- **200**: 请求成功 (SUCCESS)
- **401**: 未授权访问 (UNAUTHORIZED)
- **500**: 服务器内部错误 (ERROR)
- **501**: 系统异常 (SYSTEM_ERROR)
- **502**: 数据不存在 (DATA_NOT_EXIST)
- **503**: 数据不一致 (DATA_NOT_CONSISTENT)

### 认证方式

- 使用 JWT Token 进行身份认证
- Token 存储在 Redis 中，支持登出时主动失效
- 请求头格式：`Authorization: Bearer {token}`

---

## 1. 用户认证模块

### 1.1 用户登录

**接口描述**: 用户账号密码登录

**请求信息**:

- **URL**: `/account/login/username`
- **Method**: `POST`

**请求参数**:

```json
{
  "username": "15160255297",
  "password": "123456"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": null,
  "data": {
    "id": 1,
    "username": "dai",
    "email": "dai5297@163.com",
    "phone": "15160255297",
    "realName": "戴",
    "idCard": "110101199001011234",
    "token": "uuid-token-string"
  }
}
```

### 1.2 用户登出

**接口描述**: 用户登出系统

**请求信息**:

- **URL**: `/account/logout`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer {token}`

**响应示例**:

```json
{
  "code": 200,
  "message": null,
  "data": null
}
```

### 1.3 获取验证码

**接口描述**: 获取登录验证码

**请求信息**:

- **URL**: `/account/captcha`
- **Method**: `GET`

**响应示例**:

```json
{
  "code": 200,
  "message": null,
  "data": "ABCD"
}
```

### 1.4 用户注册

**接口描述**: 用户注册新账号

**请求信息**:

- **URL**: `/account/register`
- **Method**: `POST`

**请求参数**:

```json
{
  "username": "newuser",
  "password": "123456",
  "email": "newuser@example.com",
  "phone": "13800138000",
  "realName": "张三",
  "idCard": "110101199001011234",
  "verificationCode": "123456"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": null,
  "data": {
    "id": 2,
    "username": "newuser",
    "email": "newuser@example.com",
    "phone": "13800138000",
    "realName": "张三",
    "idCard": "110101199001011234"
  }
}
```

### 1.5 重置密码

**接口描述**: 用户重置密码

**请求信息**:

- **URL**: `/account/password/reset`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer {token}`

**请求参数**:

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": null,
  "data": null
}
```

---

## 2. 用户信息管理模块

### 2.1 获取用户信息

**接口描述**: 获取当前登录用户的详细信息

**请求信息**:

- **URL**: `/user/info`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer {token}`

**响应示例**:

```json
{
  "code": 200,
  "message": null,
  "data": {
    "id": 1,
    "username": "dai",
    "birthday": "2020-01-01",
    "address": "中国",
    "introduction": "程序员",
    "icon": "https://www.baidu.com",
    "email": "dai5297@163.com",
    "phone": "15160255297",
    "realName": "戴",
    "gender": "男",
    "idCard": "110101199001011234",
    "lastLoginTime": "2024-01-15T10:30:00"
  }
}
```

### 2.2 更新用户信息

**接口描述**: 更新用户个人信息

**请求信息**:

- **URL**: `/user/info/update`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer {token}`

**请求参数**:

```json
{
  "id": 1,
  "username": "dai",
  "birthday": "2020-01-01",
  "address": "中国",
  "introduction": "程序员",
  "icon": "https://www.baidu.com",
  "email": "dai5297@163.com",
  "phone": "15160255297",
  "realName": "戴",
  "gender": "男",
  "idCard": "110101199001011234"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": null,
  "data": {
    "id": 1,
    "username": "dai",
    "birthday": "2020-01-01",
    "address": "中国",
    "introduction": "程序员",
    "icon": "https://www.baidu.com",
    "email": "dai5297@163.com",
    "phone": "15160255297",
    "realName": "戴",
    "gender": "男",
    "idCard": "110101199001011234"
  }
}
```

---

## 3. 系统架构说明

### 3.1 微服务架构

- **网关服务**: `rs-gateway` - 统一入口，路由转发
- **用户服务**: `rs-customer` - 用户认证和信息管理
- **工具模块**: `rs-util` - 公共工具和配置

### 3.2 技术栈

- **框架**: Spring Boot 3.x + Spring Cloud
- **数据库**: MySQL + MyBatis
- **缓存**: Redis
- **认证**: JWT Token
- **文档**: Knife4j (Swagger)
- **服务发现**: Nacos

### 3.3 数据库设计

用户表 (user) 主要字段：

- `id`: 用户 ID (主键)
- `username`: 用户名
- `password`: 密码 (加密存储)
- `email`: 邮箱
- `phone`: 手机号
- `real_name`: 真实姓名
- `id_card`: 身份证号
- `birthday`: 生日
- `gender`: 性别
- `address`: 地址
- `introduction`: 个人简介
- `icon`: 头像 URL
- `create_time`: 创建时间
- `update_time`: 更新时间

---

## 4. 开发指南

### 4.1 环境配置

- **JDK**: 24
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Nacos**: 2.x

### 4.2 本地启动

1. 启动 Nacos 服务
2. 配置 MySQL 数据库
3. 启动 Redis 服务
4. 启动网关服务 (端口: 18080)
5. 启动用户服务 (端口: 18081)

### 4.3 API 文档访问

- **Knife4j 文档**: `http://localhost:18081/doc.html`
- **OpenAPI 规范**: `http://localhost:18081/v3/api-docs`

### 4.4 错误处理

系统采用全局异常处理机制：

- `GlobalExceptionHandler`: 统一异常处理
- `GlobalResultHandler`: 统一响应封装
- `CommonException`: 自定义业务异常

### 4.5 安全机制

- 密码使用 Spring Security 加密存储
- JWT Token 存储在 Redis 中，支持主动失效
- 用户信息通过拦截器自动注入上下文

---

## 5. 扩展计划

### 5.1 待开发模块

- **订单管理**: 车票预订、支付、退改签
- **车次查询**: 列车时刻表、余票查询
- **支付系统**: 多种支付方式集成
- **消息通知**: 短信、邮件、站内信
- **积分系统**: 积分获取、兑换、等级
- **联系人管理**: 常用联系人维护

### 5.2 性能优化

- 数据库读写分离
- Redis 集群部署
- 接口缓存策略
- 分布式锁机制

### 5.3 监控运维

- 链路追踪 (Sleuth + Zipkin)
- 服务监控 (Prometheus + Grafana)
- 日志聚合 (ELK Stack)
- 健康检查机制

---

## 错误码说明

| 错误码 | 说明             |
| ------ | ---------------- |
| 10001  | 用户名或密码错误 |
| 10002  | 验证码错误       |
| 10003  | 账号已被锁定     |
| 10004  | 账号不存在       |
| 10005  | 手机号已被注册   |
| 10006  | 邮箱已被注册     |
| 50001  | 原密码错误       |

---

## 接口调用示例

### JavaScript 示例

```javascript
// 用户登录
const login = async (username, password, captcha, captchaId) => {
  const response = await fetch("/api/v1/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username,
      password,
      captcha,
      captchaId,
    }),
  });

  const result = await response.json();
  if (result.code === 200) {
    localStorage.setItem("token", result.data.token);
    return result.data;
  } else {
    throw new Error(result.message);
  }
};

// 获取用户信息
const getUserProfile = async () => {
  const token = localStorage.getItem("token");
  const response = await fetch("/api/v1/user/profile", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return await response.json();
};
```

---

## 版本更新记录

### v1.0.0 (2024-01-15)

- 初始版本发布
- 包含用户认证和用户信息管理等核心功能接口

---

_本文档基于后端项目实际实现，涵盖了用户认证和用户信息管理等已实现的功能需求。_
