# 个人信息页面接口文档

## 基础信息
- **Base URL**: `/api` (根据实际网关配置)
- **认证方式**: Bearer Token
- **Header**: `Authorization: Bearer <token>`

## 1. 获取管理员信息
获取当前登录管理员的详细信息。

- **URL**: `/admin/info`
- **Method**: `GET`
- **描述**: 获取当前登录用户的个人信息，用于页面展示。

### 响应参数 (Response)
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "username": "admin",
    "realName": "张三",
    "icon": "https://example.com/avatar.png",
    "role": 103,
    "roleName": "系统管理员",
    "status": 1,
    "createTime": "2023-01-01 12:00:00"
  }
}
```

## 2. 更新个人信息
更新管理员的基本信息（如真实姓名）。

- **URL**: `/admin/profile`
- **Method**: `PUT`
- **描述**: 用户修改个人资料。

### 请求参数 (Request Body)
```json
{
  "realName": "李四"
}
```

### 响应参数 (Response)
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

## 3. 修改密码
修改当前用户的登录密码。

- **URL**: `/admin/password/change`
- **Method**: `POST`
- **描述**: 修改密码，修改成功后通常需要用户重新登录。

### 请求参数 (Request Body)
```json
{
  "oldPassword": "old_password_123",
  "newPassword": "new_password_456"
}
```

### 响应参数 (Response)
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

## 4. 上传头像
上传新的用户头像。

- **URL**: `/admin/avatar/upload`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **描述**: 上传图片文件，返回图片的访问 URL。

### 请求参数 (Form Data)
- `file`: 图片文件 (Binary)

### 响应参数 (Response)
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://oss.example.com/avatars/new_avatar.png"
  }
}
```

## 状态码说明
- `200`: 成功
- `401`: 未授权（Token 无效或过期）
- `403`: 无权限
- `400`: 请求参数错误（如密码不符合规则、原密码错误）
- `500`: 服务器内部错误
