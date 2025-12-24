# 车票管理API接口文档

## 概述

车票管理模块提供车票查询、预订、支付、退票等核心功能的API接口。基于现有的实体类架构设计，包含车票(Ticket)、车站(Station)、线路(Line)、列车(Train)和车票库存(TicketInventory)等核心实体。

## 基础信息

- **服务名称**: rs-ticket
- **基础路径**: `/api/v1/ticket`
- **认证方式**: Bearer Token
- **数据格式**: JSON

## 数据模型

### 车票实体 (Ticket)
```json
{
  "id": "车票ID (Long)",
  "lineId": "线路ID (Long)",
  "trainId": "列车ID (Long)", 
  "startTime": "出发时间 (LocalDateTime)",
  "endTime": "到达时间 (LocalDateTime)",
  "status": "状态 (String: 0-未开售, 1-正在售票, 2-售空)",
  "createBy": "创建人ID (Long)",
  "createTime": "创建时间 (LocalDateTime)",
  "updateBy": "更新人ID (Long)",
  "updateTime": "更新时间 (LocalDateTime)"
}
```

### 车票库存实体 (TicketInventory)
```json
{
  "id": "库存ID (Long)",
  "ticketId": "关联车票ID (Long)",
  "seatType": "座位类型 (String: 0-商务座, 1-一等座, 2-二等座)",
  "price": "价格 (Double)",
  "totalSeats": "总座位数 (Integer)",
  "remainingSeats": "剩余座位数 (Integer)",
  "inventoryStatus": "库存状态 (String: 0-充足, 1-紧张, 2-售空)"
}
```

## API接口

### 1. 车票查询

#### 1.1 查询车票列表
**接口地址**: `GET /api/v1/ticket/search`

**请求参数**:
```json
{
  "startStationId": "出发站ID (Long, 必填)",
  "endStationId": "到达站ID (Long, 必填)", 
  "departureDate": "出发日期 (LocalDate, 必填)",
  "trainType": "列车类型 (String, 可选: G/D/C/K等)",
  "pageNum": "页码 (Integer, 默认1)",
  "pageSize": "每页大小 (Integer, 默认10)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 50,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 5,
    "list": [
      {
        "ticketId": 1001,
        "trainCode": "G1234",
        "trainName": "复兴号",
        "trainType": "G",
        "startStation": {
          "id": 1,
          "name": "北京南",
          "code": "VNP"
        },
        "endStation": {
          "id": 2,
          "name": "上海虹桥", 
          "code": "AOH"
        },
        "startTime": "2024-01-15T08:00:00",
        "endTime": "2024-01-15T12:30:00",
        "duration": "4小时30分",
        "status": "1",
        "seatTypes": [
          {
            "seatType": "0",
            "seatTypeName": "商务座",
            "price": 1748.0,
            "remainingSeats": 8,
            "inventoryStatus": "0"
          },
          {
            "seatType": "1", 
            "seatTypeName": "一等座",
            "price": 933.0,
            "remainingSeats": 28,
            "inventoryStatus": "0"
          },
          {
            "seatType": "2",
            "seatTypeName": "二等座", 
            "price": 553.0,
            "remainingSeats": 99,
            "inventoryStatus": "0"
          }
        ]
      }
    ]
  }
}
```

#### 1.2 获取车票详情
**接口地址**: `GET /api/v1/ticket/{ticketId}`

**路径参数**:
- `ticketId`: 车票ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1001,
    "lineId": 1,
    "trainId": 1,
    "startTime": "2024-01-15T08:00:00",
    "endTime": "2024-01-15T12:30:00",
    "status": "1",
    "train": {
      "id": 1,
      "name": "复兴号",
      "code": "G1234",
      "type": "G"
    },
    "line": {
      "id": 1,
      "name": "京沪高铁",
      "code": "JSHSR",
      "startStation": 1,
      "endStation": 2
    },
    "inventories": [
      {
        "id": 1,
        "seatType": "0",
        "price": 1748.0,
        "totalSeats": 50,
        "remainingSeats": 8,
        "inventoryStatus": "0"
      }
    ]
  }
}
```

### 2. 车票预订

#### 2.1 创建订单
**接口地址**: `POST /api/v1/ticket/order`

**请求参数**:
```json
{
  "ticketId": "车票ID (Long, 必填)",
  "seatType": "座位类型 (String, 必填)",
  "passengers": [
    {
      "name": "乘客姓名 (String, 必填)",
      "idCard": "身份证号 (String, 必填)",
      "phone": "手机号 (String, 必填)",
      "passengerType": "乘客类型 (String: 0-成人, 1-儿童, 2-学生)"
    }
  ],
  "contactInfo": {
    "name": "联系人姓名 (String, 必填)",
    "phone": "联系人手机号 (String, 必填)",
    "email": "联系人邮箱 (String, 可选)"
  }
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "orderId": "202401151234567890",
    "ticketId": 1001,
    "totalAmount": 1106.0,
    "passengerCount": 2,
    "status": "PENDING_PAYMENT",
    "expireTime": "2024-01-15T08:30:00",
    "paymentUrl": "https://pay.example.com/order/202401151234567890"
  }
}
```

#### 2.2 查询订单状态
**接口地址**: `GET /api/v1/ticket/order/{orderId}`

**路径参数**:
- `orderId`: 订单ID (String)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "orderId": "202401151234567890",
    "ticketId": 1001,
    "status": "PAID",
    "totalAmount": 1106.0,
    "createTime": "2024-01-15T08:00:00",
    "payTime": "2024-01-15T08:05:00",
    "passengers": [
      {
        "name": "张三",
        "idCard": "110101199001011234",
        "seatNumber": "01车06A"
      }
    ]
  }
}
```

### 3. 车票管理 (管理员接口)

#### 3.1 创建车票
**接口地址**: `POST /api/v1/ticket/admin`

**请求参数**:
```json
{
  "lineId": "线路ID (Long, 必填)",
  "trainId": "列车ID (Long, 必填)",
  "startTime": "出发时间 (LocalDateTime, 必填)",
  "endTime": "到达时间 (LocalDateTime, 必填)",
  "seatConfigs": [
    {
      "seatType": "座位类型 (String, 必填)",
      "price": "价格 (Double, 必填)",
      "totalSeats": "总座位数 (Integer, 必填)"
    }
  ]
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "车票创建成功",
  "data": {
    "ticketId": 1001,
    "status": "0"
  }
}
```

#### 3.2 更新车票状态
**接口地址**: `PUT /api/v1/ticket/admin/{ticketId}/status`

**路径参数**:
- `ticketId`: 车票ID (Long)

**请求参数**:
```json
{
  "status": "状态 (String: 0-未开售, 1-正在售票, 2-售空)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "状态更新成功"
}
```

#### 3.3 删除车票
**接口地址**: `DELETE /api/v1/ticket/admin/{ticketId}`

**路径参数**:
- `ticketId`: 车票ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "车票删除成功"
}
```

### 4. 库存管理

#### 4.1 查询库存信息
**接口地址**: `GET /api/v1/ticket/{ticketId}/inventory`

**路径参数**:
- `ticketId`: 车票ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "ticketId": 1001,
      "seatType": "0",
      "seatTypeName": "商务座",
      "price": 1748.0,
      "totalSeats": 50,
      "remainingSeats": 8,
      "inventoryStatus": "0"
    }
  ]
}
```

#### 4.2 更新库存 (管理员接口)
**接口地址**: `PUT /api/v1/ticket/admin/inventory/{inventoryId}`

**路径参数**:
- `inventoryId`: 库存ID (Long)

**请求参数**:
```json
{
  "price": "价格 (Double, 可选)",
  "totalSeats": "总座位数 (Integer, 可选)",
  "remainingSeats": "剩余座位数 (Integer, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "库存更新成功"
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 409 | 资源冲突 (如座位已被预订) |
| 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 10001 | 车票不存在 |
| 10002 | 车票未开售 |
| 10003 | 车票已售空 |
| 10004 | 座位类型不存在 |
| 10005 | 座位数量不足 |
| 10006 | 订单已过期 |
| 10007 | 订单状态异常 |
| 10008 | 乘客信息不完整 |
| 10009 | 身份证号格式错误 |
| 10010 | 手机号格式错误 |

## 注意事项

1. **认证授权**: 所有接口都需要在请求头中携带有效的Bearer Token
2. **时间格式**: 所有时间字段使用ISO 8601格式 (yyyy-MM-ddTHH:mm:ss)
3. **分页查询**: 默认页码从1开始，最大每页大小为100
4. **订单有效期**: 未支付订单默认30分钟后自动取消
5. **库存锁定**: 创建订单时会临时锁定座位，支付成功后正式扣减库存
6. **并发控制**: 使用乐观锁机制防止超卖问题
7. **数据一致性**: 关键操作使用事务保证数据一致性

## 版本历史

- **v1.0.0** (2024-01-15): 初始版本，包含基础的车票查询和预订功能