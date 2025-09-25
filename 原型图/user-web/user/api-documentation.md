# 个人中心接口文档

## 概述

本文档描述了个人中心相关的 API 接口，包括用户信息管理、账号安全、订单管理等功能模块。

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **认证方式**: Bearer Token (在请求头中添加 `Authorization: Bearer {token}`)

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

## 3. 订单管理

### 3.1 获取订单列表

**接口地址**: `GET /order/list`

**请求参数**:

- `page`: 页码 (默认: 1)
- `size`: 每页数量 (默认: 10)
- `status`: 订单状态 (可选)

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "pages": 5,
    "current": 1,
    "size": 10,
    "records": [
      {
        "orderId": "202401010001",
        "trainNumber": "G123",
        "departure": "北京南",
        "arrival": "上海虹桥",
        "departureTime": "2024-01-15 08:00:00",
        "arrivalTime": "2024-01-15 12:30:00",
        "seatType": "二等座",
        "price": 553.0,
        "status": "已支付",
        "createTime": "2024-01-10 10:30:00"
      }
    ]
  }
}
```

### 3.2 获取订单详情

**接口地址**: `GET /order/{orderId}`

**路径参数**:

- `orderId`: 订单 ID

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": "202401010001",
    "trainNumber": "G123",
    "departure": "北京南",
    "arrival": "上海虹桥",
    "departureTime": "2024-01-15 08:00:00",
    "arrivalTime": "2024-01-15 12:30:00",
    "seatType": "二等座",
    "seatNumber": "03车06A",
    "price": 553.0,
    "status": "已支付",
    "passengers": [
      {
        "name": "张三",
        "idCard": "110101199001011234",
        "phone": "13800138000"
      }
    ],
    "createTime": "2024-01-10 10:30:00",
    "payTime": "2024-01-10 10:35:00"
  }
}
```

---

## 4. 常用联系人管理

### 4.1 获取联系人列表

**接口地址**: `GET /contact/page`

**请求参数**:

| 参数名        | 类型    | 必填 | 默认值 | 说明                   |
| ------------- | ------- | ---- | ------ | ---------------------- |
| page          | Integer | 否   | 1      | 页码，从 1 开始        |
| size          | Integer | 否   | 10     | 每页数量，最大 100     |
| name          | String  | 否   | -      | 联系人姓名（模糊查询） |
| passengerType | Integer | 否   | -      | 乘客类型筛选           |
| status        | Integer | 否   | -      | 状态筛选               |

**请求示例**:

```
GET /contact/page?page=1&size=10&name=张&passengerType=1&status=1
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 25,
    "pages": 3,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "name": "张三",
        "idCard": "110101199001011234",
        "phone": "13800138000",
        "email": "zhangsan@example.com",
        "passengerType": 1,
        "isDefault": 1,
        "status": 1,
        "remark": "常用联系人",
        "createTime": "2024-01-10 10:30:00",
        "updateTime": "2024-01-15 14:20:00"
      },
      {
        "id": 2,
        "name": "李四",
        "idCard": "110101199001011235",
        "phone": "13800138001",
        "email": "lisi@example.com",
        "passengerType": 2,
        "isDefault": 0,
        "status": 1,
        "remark": "儿童票",
        "createTime": "2024-01-12 09:15:00",
        "updateTime": "2024-01-12 09:15:00"
      }
    ]
  }
}
```

### 4.2 添加联系人

**接口地址**: `POST /contact/add`

**请求参数**:

```json
{
  "name": "王五",
  "idCard": "110101199001011236",
  "phone": "13800138002",
  "email": "wangwu@example.com",
  "passengerType": 1,
  "isDefault": 0,
  "status": 1,
  "remark": "新增联系人"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "联系人添加成功",
  "data": {
    "id": 3,
    "name": "王五",
    "idCard": "110101199001011236",
    "phone": "13800138002",
    "email": "wangwu@example.com",
    "passengerType": 1,
    "isDefault": 0,
    "status": 1,
    "remark": "新增联系人",
    "createTime": "2024-01-20 16:30:00",
    "updateTime": "2024-01-20 16:30:00"
  }
}
```

### 4.3 更新联系人

**接口地址**: `PUT /contact/{contactId}`

**路径参数**:

- `contactId`: 联系人 ID

**请求参数**:

```json
{
  "name": "王五",
  "idCard": "110101199001011236",
  "phone": "13800138002",
  "email": "wangwu_updated@example.com",
  "passengerType": 3,
  "isDefault": 1,
  "status": 1,
  "remark": "更新后的联系人信息"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "联系人更新成功",
  "data": {
    "id": 3,
    "name": "王五",
    "idCard": "110101199001011236",
    "phone": "13800138002",
    "email": "wangwu_updated@example.com",
    "passengerType": 3,
    "isDefault": 1,
    "status": 1,
    "remark": "更新后的联系人信息",
    "createTime": "2024-01-20 16:30:00",
    "updateTime": "2024-01-21 10:15:00"
  }
}
```

### 4.4 删除联系人

**接口地址**: `DELETE /contact/{contactId}`

**路径参数**:

- `contactId`: 联系人 ID

**响应示例**:

```json
{
  "code": 200,
  "message": "联系人删除成功",
  "data": null
}
```

---

## 5. 积分管理

### 5.1 获取积分信息

**接口地址**: `GET /points/info`

**请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalPoints": 1250,
    "availablePoints": 1000,
    "frozenPoints": 250,
    "level": "银牌会员",
    "nextLevelPoints": 2000
  }
}
```

### 5.2 获取积分明细

**接口地址**: `GET /points/history`

**请求参数**:

- `page`: 页码 (默认: 1)
- `size`: 每页数量 (默认: 10)
- `type`: 积分类型 (可选: earn, spend)

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 20,
    "pages": 2,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "type": "earn",
        "points": 100,
        "description": "购票获得积分",
        "orderId": "202401010001",
        "createTime": "2024-01-10 10:35:00"
      },
      {
        "id": 2,
        "type": "spend",
        "points": -50,
        "description": "积分兑换优惠券",
        "createTime": "2024-01-08 15:20:00"
      }
    ]
  }
}
```

---

## 6. 数据字典

### 6.1 证件类型 (idType)

- `0`: 身份证
- `1`: 护照
- `2`: 军官证
- `3`: 警官证
- `4`: 台胞证
- `5`: 港澳通行证

### 6.2 乘客类型 (passengerType)

- `1`: 成人
- `2`: 儿童
- `3`: 学生
- `4`: 老人

### 6.3 联系人状态 (status)

- `0`: 禁用
- `1`: 启用

### 6.4 默认联系人标识 (isDefault)

- `0`: 非默认联系人
- `1`: 默认联系人

### 6.5 删除标识 (isDeleted)

- `0`: 未删除
- `1`: 已删除

### 6.6 验证状态 (verifyStatus)

- `0`: 未验证
- `1`: 已验证
- `2`: 验证失败

### 6.7 订单状态

- `待支付`: 订单已创建，等待支付
- `已支付`: 订单已支付成功
- `已出票`: 车票已出票
- `已取消`: 订单已取消
- `已退票`: 订单已退票
- `改签中`: 订单改签处理中

---

## 7. 错误码说明

| 错误码 | 说明             |
| ------ | ---------------- |
| 200    | 请求成功         |
| 400    | 请求参数错误     |
| 401    | 未授权，需要登录 |
| 403    | 禁止访问         |
| 404    | 资源不存在       |
| 500    | 服务器内部错误   |
| 1001   | 用户名或密码错误 |
| 1002   | 用户不存在       |
| 1003   | 用户已存在       |
| 1004   | 验证码错误       |
| 1005   | 手机号格式错误   |
| 1006   | 邮箱格式错误     |
| 1007   | 身份证格式错误   |

---

## 8. 注意事项

1. 所有需要认证的接口都需要在请求头中携带有效的 Token
2. 时间格式统一使用 `yyyy-MM-dd HH:mm:ss`
3. 金额单位为元，保留两位小数
4. 分页查询的页码从 1 开始
5. 所有接口都支持跨域请求
6. 建议使用 HTTPS 协议保证数据传输安全

---

## 9. 更新日志

| 版本 | 日期       | 更新内容                                                                                                      |
| ---- | ---------- | ------------------------------------------------------------------------------------------------------------- |
| v1.0 | 2024-01-24 | 初始版本，包含基础用户管理、账号安全、订单管理等接口                                                          |
| v1.1 | 2024-01-25 | 更新联系人管理接口，新增 email、passengerType、isDefault、status、remark 字段，优化数据字典定义，移除过时字段 |
| v1.2 | 2024-01-26 | 修正联系人分页查询接口 URL 路径为 `/contact/page`，确保与后端 Controller 路径一致                             |
