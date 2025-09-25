# 用户管理API接口文档

## 概述

本文档描述了仿12306铁路微服务系统中用户管理相关的API接口，包括用户注册、登录、信息管理等功能。

## 基础信息

- **基础URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token
- **数据格式**: JSON
- **字符编码**: UTF-8

## 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-01T12:00:00"
}
```

### 响应状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 用户实体模型

```json
{
  "id": 1,
  "username": "user123",
  "gender": "男",
  "birthday": "1990-01-01T00:00:00",
  "address": "北京市朝阳区",
  "introduction": "这是我的个性签名",
  "icon": "https://example.com/avatar.jpg",
  "email": "user@example.com",
  "phone": "13800138000",
  "realName": "张三",
  "idCard": "110101199001011234",
  "createTime": "2024-01-01T12:00:00",
  "updateTime": "2024-01-01T12:00:00"
}
```

## API接口列表

### 1. 用户注册

**接口地址**: `POST /user/register`

**接口描述**: 用户注册新账号

**请求参数**:
```json
{
  "username": "user123",
  "password": "password123",
  "email": "user@example.com",
  "phone": "13800138000",
  "realName": "张三",
  "idCard": "110101199001011234",
  "code": "123456"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名，3-20位字符 |
| password | string | 是 | 密码，6-20位字符 |
| email | string | 是 | 邮箱地址 |
| phone | string | 是 | 手机号码 |
| realName | string | 是 | 真实姓名 |
| idCard | string | 是 | 身份证号码 |
| code | string | 是 | 短信验证码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "user123",
    "email": "user@example.com",
    "phone": "13800138000",
    "realName": "张三",
    "idCard": "110101199001011234"
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 2. 用户登录

**接口地址**: `POST /user/login/username`

**接口描述**: 用户名密码登录

**请求参数**:
```json
{
  "username": "user123",
  "password": "password123",
  "captcha": "ABCD",
  "rememberMe": true
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名或手机号 |
| password | string | 是 | 密码 |
| captcha | string | 否 | 图形验证码 |
| rememberMe | boolean | 否 | 是否记住登录状态 |

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "phone": "13800138000",
      "realName": "张三",
      "idCard": "110101199001011234",
      "points": 1000,
      "level": "普通会员",
      "status": "正常",
      "createdAt": "2024-01-01T12:00:00",
      "lastLoginAt": "2024-01-01T12:00:00"
    }
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 3. 获取用户信息

**接口地址**: `GET /user/info`

**接口描述**: 获取当前登录用户的详细信息

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "user123",
    "gender": "男",
    "birthday": "1990-01-01T00:00:00",
    "address": "北京市朝阳区",
    "introduction": "这是我的个性签名",
    "icon": "https://example.com/avatar.jpg",
    "email": "user@example.com",
    "phone": "13800138000",
    "realName": "张三",
    "idCard": "110101199001011234",
    "points": 1000,
    "level": "普通会员",
    "status": "正常",
    "createTime": "2024-01-01T12:00:00",
    "updateTime": "2024-01-01T12:00:00"
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 4. 更新用户信息

**接口地址**: `POST /user/info/update`

**接口描述**: 更新用户个人信息

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "gender": "男",
  "birthday": "1990-01-01T00:00:00",
  "address": "北京市朝阳区",
  "introduction": "这是我的个性签名",
  "icon": "https://example.com/avatar.jpg",
  "email": "newemail@example.com"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| gender | string | 否 | 性别：男/女 |
| birthday | string | 否 | 生日，ISO格式 |
| address | string | 否 | 地址 |
| introduction | string | 否 | 个性签名 |
| icon | string | 否 | 头像URL |
| email | string | 否 | 邮箱地址 |

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "username": "user123",
    "gender": "男",
    "birthday": "1990-01-01T00:00:00",
    "address": "北京市朝阳区",
    "introduction": "这是我的个性签名",
    "icon": "https://example.com/avatar.jpg",
    "email": "newemail@example.com",
    "phone": "13800138000",
    "realName": "张三",
    "idCard": "110101199001011234",
    "updateTime": "2024-01-01T12:30:00"
  },
  "timestamp": "2024-01-01T12:30:00"
}
```

### 5. 修改密码

**接口地址**: `PUT /user/password/reset`

**接口描述**: 修改用户密码

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "oldPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 原密码 |
| newPassword | string | 是 | 新密码，6-20位字符 |

**响应示例**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

### 6. 用户登出

**接口地址**: `POST /user/logout`

**接口描述**: 用户登出系统

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

### 7. 发送短信验证码

**接口地址**: `POST /user/sms`

**接口描述**: 发送短信验证码

**请求参数**:
```json
{
  "phone": "13800138000"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | string | 是 | 手机号码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码发送成功",
  "data": {
    "expireTime": 300
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 8. 通过手机号重置密码

**接口地址**: `POST /user/reset-password`

**接口描述**: 通过手机号和验证码重置密码

**请求参数**:
```json
{
  "phone": "13800138000",
  "code": "123456",
  "newPassword": "newpassword123"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | string | 是 | 手机号码 |
| code | string | 是 | 短信验证码 |
| newPassword | string | 是 | 新密码，6-20位字符 |

**响应示例**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

### 9. 获取图形验证码

**接口地址**: `GET /user/captcha`

**接口描述**: 获取图形验证码

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "captchaId": "uuid-string",
    "captchaImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 10001 | 用户名已存在 |
| 10002 | 手机号已注册 |
| 10003 | 邮箱已注册 |
| 10004 | 验证码错误或已过期 |
| 10005 | 用户名或密码错误 |
| 10006 | 账号已被禁用 |
| 10007 | 原密码错误 |
| 10008 | 图形验证码错误 |
| 10009 | 短信发送失败 |
| 10010 | 身份证号格式错误 |

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带JWT Token
2. 密码需要进行MD5加密后传输
3. 身份证号需要进行格式验证
4. 手机号需要进行格式验证
5. 验证码有效期为5分钟
6. 同一手机号每分钟最多发送一次验证码
7. 登录失败超过5次将锁定账号30分钟

## 更新日志

- v1.0.0 (2024-01-01): 初始版本，包含基础用户管理功能