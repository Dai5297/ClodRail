# 车票系统API接口文档

## 📋 文档概述

本文档基于车票系统原型图功能分析，定义了车票查询、预订、管理等核心功能的RESTful API接口规范。

**版本**: v1.0  
**基础URL**: `https://api.railway-system.com/v1`  
**认证方式**: Bearer Token  
**数据格式**: JSON  

---

## 🎯 1. 车票查询相关接口

### 1.1 车票搜索查询

**接口描述**: 根据出发地、目的地、日期等条件查询可用车票

```http
POST /tickets/search
```

**请求参数**:
```json
{
  "departureCity": "北京",           // 出发城市 (必填)
  "arrivalCity": "上海",             // 到达城市 (必填)
  "departureDate": "2024-03-15",     // 出发日期 YYYY-MM-DD (必填)
  "returnDate": "2024-03-20",        // 返程日期 YYYY-MM-DD (可选，往返票)
  "trainType": "HIGH_SPEED",         // 列车类型: HIGH_SPEED(高速动车), NORMAL(火车), ALL(全部)
  "seatTypes": ["BUSINESS", "FIRST", "SECOND"], // 座位类型筛选 (可选)
  "page": 1,                         // 页码 (默认1)
  "size": 20                         // 每页数量 (默认20)
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 156,
    "page": 1,
    "size": 20,
    "tickets": [
      {
        "trainNumber": "G123",
        "trainType": "HIGH_SPEED",
        "trainTypeName": "高速动车",
        "departureStation": "北京南",
        "arrivalStation": "上海虹桥",
        "departureTime": "08:00",
        "arrivalTime": "13:28",
        "duration": "5小时28分",
        "distance": "1318公里",
        "seats": [
          {
            "seatType": "BUSINESS",
            "seatTypeName": "商务座",
            "price": 1748.00,
            "availableCount": 12,
            "status": "AVAILABLE"  // AVAILABLE(有票), TIGHT(紧张), SOLD_OUT(售完)
          },
          {
            "seatType": "FIRST",
            "seatTypeName": "一等座",
            "price": 933.00,
            "availableCount": 3,
            "status": "TIGHT"
          },
          {
            "seatType": "SECOND",
            "seatTypeName": "二等座",
            "price": 553.00,
            "availableCount": 0,
            "status": "SOLD_OUT"
          }
        ],
        "canBook": true,
        "remarks": "可选座"
      }
    ]
  }
}
```

### 1.2 获取城市/车站列表

**接口描述**: 获取支持的城市和车站信息，支持搜索

```http
GET /stations?keyword={keyword}&type={type}
```

**请求参数**:
- `keyword`: 搜索关键词 (可选)
- `type`: 类型筛选 CITY(城市) | STATION(车站) | ALL(全部，默认)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": "BJS",
      "name": "北京",
      "type": "CITY",
      "stations": [
        {
          "id": "BJP",
          "name": "北京",
          "fullName": "北京站"
        },
        {
          "id": "VNP",
          "name": "北京南",
          "fullName": "北京南站"
        }
      ]
    }
  ]
}
```

### 1.3 获取车票详情

**接口描述**: 获取指定车次的详细信息

```http
GET /tickets/{trainNumber}/detail?date={date}&departure={departure}&arrival={arrival}
```

**请求参数**:
- `trainNumber`: 车次号
- `date`: 出发日期 YYYY-MM-DD
- `departure`: 出发站代码
- `arrival`: 到达站代码

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "trainNumber": "G123",
    "trainType": "HIGH_SPEED",
    "trainTypeName": "高速动车",
    "departureStation": "北京南",
    "arrivalStation": "上海虹桥",
    "departureTime": "08:00",
    "arrivalTime": "13:28",
    "duration": "5小时28分",
    "distance": "1318公里",
    "stops": [
      {
        "stationName": "北京南",
        "arrivalTime": "始发站",
        "departureTime": "08:00",
        "stopDuration": "—",
        "isStart": true,
        "isEnd": false
      },
      {
        "stationName": "济南西",
        "arrivalTime": "09:23",
        "departureTime": "09:25",
        "stopDuration": "2分",
        "isStart": false,
        "isEnd": false
      },
      {
        "stationName": "上海虹桥",
        "arrivalTime": "13:28",
        "departureTime": "终点站",
        "stopDuration": "—",
        "isStart": false,
        "isEnd": true
      }
    ],
    "seats": [
      {
        "seatType": "BUSINESS",
        "seatTypeName": "商务座",
        "price": 1748.00,
        "availableCount": 12,
        "status": "AVAILABLE",
        "features": ["可选座", "免费餐食", "优先检票"]
      }
    ],
    "services": [
      "免费WiFi",
      "充电插座",
      "餐车服务"
    ],
    "notices": [
      "请提前30分钟到达车站",
      "携带有效身份证件"
    ]
  }
}
```

---

## 🎫 2. 车票预订相关接口

### 2.1 创建订单

**接口描述**: 创建车票预订订单

```http
POST /orders
```

**请求参数**:
```json
{
  "trainNumber": "G123",
  "departureDate": "2024-03-15",
  "departureStation": "北京南",
  "arrivalStation": "上海虹桥",
  "passengers": [
    {
      "contactId": "12345",          // 联系人ID (已存在联系人)
      "name": "张三",                // 姓名
      "idType": "ID_CARD",           // 证件类型
      "idNumber": "110101199001011234", // 证件号码
      "phone": "13800138000",        // 手机号
      "seatType": "FIRST",           // 座位类型
      "seatPosition": "A",           // 座位位置偏好 A(靠窗) B(靠过道) C(中间) D(靠过道) F(靠窗)
      "ticketType": "ADULT"          // 票种类型 ADULT(成人) CHILD(儿童) STUDENT(学生)
    }
  ],
  "insurance": {
    "enabled": true,                 // 是否购买保险
    "type": "ACCIDENT",              // 保险类型
    "amount": 20.00                  // 保险费用
  },
  "contactInfo": {
    "name": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com"
  }
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "orderId": "ORD202403151234567890",
    "orderStatus": "PENDING_PAYMENT",
    "totalAmount": 953.00,
    "paymentDeadline": "2024-03-15T09:30:00",
    "tickets": [
      {
        "ticketId": "TKT202403151234567891",
        "passengerName": "张三",
        "seatNumber": "01车06A",
        "price": 933.00
      }
    ],
    "insurance": {
      "enabled": true,
      "amount": 20.00
    }
  }
}
```

### 2.2 获取联系人列表

**接口描述**: 获取用户的联系人列表

```http
GET /contacts?page={page}&size={size}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 5,
    "contacts": [
      {
        "id": "12345",
        "name": "张三",
        "idType": "ID_CARD",
        "idTypeName": "身份证",
        "idNumber": "110101199001011234",
        "phone": "13800138000",
        "isDefault": true,
        "ticketTypes": ["ADULT"],
        "createTime": "2024-01-15T10:30:00"
      }
    ]
  }
}
```

### 2.3 添加联系人

**接口描述**: 添加新的联系人信息

```http
POST /contacts
```

**请求参数**:
```json
{
  "name": "李四",
  "idType": "ID_CARD",
  "idNumber": "110101199002021234",
  "phone": "13900139000",
  "isDefault": false
}
```

### 2.4 获取座位选择信息

**接口描述**: 获取指定车次和座位类型的座位选择信息

```http
GET /tickets/{trainNumber}/seats?date={date}&seatType={seatType}&count={count}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "seatMap": {
      "carNumber": "01",
      "seatType": "FIRST",
      "layout": "2+2",
      "positions": [
        {
          "position": "A",
          "description": "靠窗",
          "available": true,
          "recommended": true
        },
        {
          "position": "C",
          "description": "靠过道",
          "available": true,
          "recommended": false
        },
        {
          "position": "D",
          "description": "靠过道",
          "available": false,
          "recommended": false
        },
        {
          "position": "F",
          "description": "靠窗",
          "available": true,
          "recommended": true
        }
      ]
    }
  }
}
```

---

## 📋 3. 订单管理相关接口

### 3.1 获取订单列表

**接口描述**: 获取用户的订单列表

```http
GET /orders?status={status}&page={page}&size={size}
```

**请求参数**:
- `status`: 订单状态筛选 (可选)
  - `PENDING_PAYMENT`: 待支付
  - `PAID`: 已支付
  - `COMPLETED`: 已完成
  - `CANCELLED`: 已取消
  - `REFUNDED`: 已退票

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 10,
    "orders": [
      {
        "orderId": "ORD202403151234567890",
        "orderStatus": "PAID",
        "orderStatusName": "已支付",
        "trainNumber": "G123",
        "departureStation": "北京南",
        "arrivalStation": "上海虹桥",
        "departureDate": "2024-03-15",
        "departureTime": "08:00",
        "passengerCount": 1,
        "totalAmount": 953.00,
        "createTime": "2024-03-14T15:30:00",
        "payTime": "2024-03-14T15:35:00",
        "canRefund": true,
        "canChange": true
      }
    ]
  }
}
```

### 3.2 获取订单详情

**接口描述**: 获取指定订单的详细信息

```http
GET /orders/{orderId}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "orderId": "ORD202403151234567890",
    "orderStatus": "PAID",
    "orderStatusName": "已支付",
    "trainInfo": {
      "trainNumber": "G123",
      "trainType": "HIGH_SPEED",
      "departureStation": "北京南",
      "arrivalStation": "上海虹桥",
      "departureDate": "2024-03-15",
      "departureTime": "08:00",
      "arrivalTime": "13:28",
      "duration": "5小时28分"
    },
    "tickets": [
      {
        "ticketId": "TKT202403151234567891",
        "passengerName": "张三",
        "idNumber": "110101199001011234",
        "seatType": "FIRST",
        "seatTypeName": "一等座",
        "seatNumber": "01车06A",
        "price": 933.00,
        "ticketStatus": "VALID"
      }
    ],
    "insurance": {
      "enabled": true,
      "type": "ACCIDENT",
      "amount": 20.00
    },
    "payment": {
      "totalAmount": 953.00,
      "payMethod": "WECHAT_PAY",
      "payTime": "2024-03-14T15:35:00",
      "transactionId": "TXN202403141535001"
    },
    "contactInfo": {
      "name": "张三",
      "phone": "13800138000",
      "email": "zhangsan@example.com"
    },
    "refundInfo": {
      "canRefund": true,
      "refundDeadline": "2024-03-15T06:00:00",
      "refundFee": 0.00
    },
    "changeInfo": {
      "canChange": true,
      "changeDeadline": "2024-03-15T06:00:00",
      "changeFee": 0.00
    }
  }
}
```

### 3.3 取消订单

**接口描述**: 取消未支付的订单

```http
POST /orders/{orderId}/cancel
```

**请求参数**:
```json
{
  "reason": "行程变更"
}
```

### 3.4 申请退票

**接口描述**: 申请退票

```http
POST /orders/{orderId}/refund
```

**请求参数**:
```json
{
  "ticketIds": ["TKT202403151234567891"],  // 要退票的车票ID列表
  "reason": "行程取消",
  "refundAccount": {
    "type": "ORIGINAL",                    // 退款方式 ORIGINAL(原路退回) BANK(银行卡)
    "accountInfo": "原支付账户"
  }
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "退票申请提交成功",
  "data": {
    "refundId": "REF202403151234567890",
    "refundAmount": 933.00,
    "refundFee": 0.00,
    "actualRefund": 933.00,
    "processTime": "1-3个工作日"
  }
}
```

### 3.5 申请改签

**接口描述**: 申请车票改签

```http
POST /orders/{orderId}/change
```

**请求参数**:
```json
{
  "ticketId": "TKT202403151234567891",
  "newTrainNumber": "G125",
  "newDepartureDate": "2024-03-16",
  "newSeatType": "FIRST",
  "reason": "行程调整"
}
```

---

## 💳 4. 支付相关接口

### 4.1 创建支付订单

**接口描述**: 为订单创建支付请求

```http
POST /payments
```

**请求参数**:
```json
{
  "orderId": "ORD202403151234567890",
  "payMethod": "WECHAT_PAY",              // 支付方式 WECHAT_PAY, ALIPAY, BANK_CARD
  "returnUrl": "https://app.railway.com/payment/return",
  "notifyUrl": "https://api.railway.com/payment/notify"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "支付订单创建成功",
  "data": {
    "paymentId": "PAY202403151234567890",
    "payUrl": "https://pay.weixin.qq.com/...",
    "qrCode": "data:image/png;base64,iVBOR...",
    "expireTime": "2024-03-15T09:30:00"
  }
}
```

### 4.2 查询支付状态

**接口描述**: 查询支付订单状态

```http
GET /payments/{paymentId}/status
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "paymentId": "PAY202403151234567890",
    "orderId": "ORD202403151234567890",
    "status": "SUCCESS",                   // PENDING, SUCCESS, FAILED, CANCELLED
    "amount": 953.00,
    "payTime": "2024-03-14T15:35:00",
    "transactionId": "TXN202403141535001"
  }
}
```

---

## 🎟️ 5. 电子票相关接口

### 5.1 获取电子票信息

**接口描述**: 获取电子车票信息

```http
GET /tickets/{ticketId}/electronic
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "ticketId": "TKT202403151234567891",
    "qrCode": "data:image/png;base64,iVBOR...",
    "ticketInfo": {
      "trainNumber": "G123",
      "departureStation": "北京南",
      "arrivalStation": "上海虹桥",
      "departureDate": "2024-03-15",
      "departureTime": "08:00",
      "seatNumber": "01车06A",
      "passengerName": "张三",
      "idNumber": "110101199001011234"
    },
    "validTime": "2024-03-15T07:00:00",
    "expireTime": "2024-03-15T14:00:00"
  }
}
```

### 5.2 下载电子票PDF

**接口描述**: 下载电子车票PDF文件

```http
GET /tickets/{ticketId}/pdf
```

**响应**: PDF文件流

---

## 📊 6. 统计分析相关接口

### 6.1 获取用户出行统计

**接口描述**: 获取用户的出行数据统计

```http
GET /statistics/travel?year={year}&month={month}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "period": "2024-03",
    "summary": {
      "totalTrips": 5,
      "totalAmount": 2865.00,
      "totalDistance": 6590,
      "favoriteRoute": "北京-上海"
    },
    "monthlyData": [
      {
        "month": "2024-01",
        "trips": 2,
        "amount": 1106.00
      }
    ],
    "routeAnalysis": [
      {
        "route": "北京-上海",
        "count": 3,
        "amount": 1799.00
      }
    ]
  }
}
```

---

## 🔧 7. 系统配置相关接口

### 7.1 获取系统配置

**接口描述**: 获取系统配置信息

```http
GET /config
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "booking": {
      "advanceDays": 30,                   // 提前预订天数
      "paymentTimeout": 30,                // 支付超时时间(分钟)
      "refundDeadline": 120,               // 退票截止时间(分钟)
      "changeDeadline": 120                // 改签截止时间(分钟)
    },
    "seatTypes": {
      "HIGH_SPEED": [
        {
          "code": "BUSINESS",
          "name": "商务座",
          "description": "宽敞舒适，免费餐食"
        },
        {
          "code": "FIRST",
          "name": "一等座",
          "description": "座椅宽敞，服务优质"
        },
        {
          "code": "SECOND",
          "name": "二等座",
          "description": "经济实惠，性价比高"
        }
      ],
      "NORMAL": [
        {
          "code": "SOFT_SLEEPER",
          "name": "软卧",
          "description": "独立包厢，舒适安静"
        },
        {
          "code": "HARD_SLEEPER",
          "name": "硬卧",
          "description": "经济实惠，适合长途"
        },
        {
          "code": "SOFT_SEAT",
          "name": "软座",
          "description": "座椅舒适，适合短途"
        },
        {
          "code": "HARD_SEAT",
          "name": "硬座",
          "description": "价格便宜，基础服务"
        }
      ]
    },
    "payMethods": [
      {
        "code": "WECHAT_PAY",
        "name": "微信支付",
        "enabled": true
      },
      {
        "code": "ALIPAY",
        "name": "支付宝",
        "enabled": true
      },
      {
        "code": "BANK_CARD",
        "name": "银行卡",
        "enabled": true
      }
    ]
  }
}
```

---

## 📝 8. 通用响应格式

### 8.1 成功响应

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-03-15T10:30:00"
}
```

### 8.2 错误响应

```json
{
  "code": 400,
  "message": "请求参数错误",
  "error": "INVALID_PARAMETER",
  "details": "出发日期不能早于当前日期",
  "timestamp": "2024-03-15T10:30:00"
}
```

### 8.3 常见错误码

| 错误码 | 说明 | 描述 |
|--------|------|------|
| 200 | SUCCESS | 操作成功 |
| 400 | BAD_REQUEST | 请求参数错误 |
| 401 | UNAUTHORIZED | 未授权访问 |
| 403 | FORBIDDEN | 禁止访问 |
| 404 | NOT_FOUND | 资源不存在 |
| 409 | CONFLICT | 资源冲突 |
| 429 | TOO_MANY_REQUESTS | 请求过于频繁 |
| 500 | INTERNAL_ERROR | 服务器内部错误 |

### 8.4 业务错误码

| 错误码 | 说明 | 描述 |
|--------|------|------|
| 10001 | TICKET_NOT_AVAILABLE | 车票已售完 |
| 10002 | SEAT_NOT_AVAILABLE | 座位不可用 |
| 10003 | ORDER_EXPIRED | 订单已过期 |
| 10004 | PAYMENT_FAILED | 支付失败 |
| 10005 | REFUND_NOT_ALLOWED | 不允许退票 |
| 10006 | CHANGE_NOT_ALLOWED | 不允许改签 |
| 10007 | DUPLICATE_ORDER | 重复下单 |
| 10008 | INVALID_PASSENGER | 乘客信息无效 |

---

## 🔐 9. 认证与授权

### 9.1 认证方式

所有API请求需要在Header中携带认证信息：

```http
Authorization: Bearer {access_token}
```

### 9.2 Token获取

通过用户登录接口获取访问令牌：

```http
POST /auth/login
```

### 9.3 Token刷新

```http
POST /auth/refresh
```

---

## 📋 10. 接口调用示例

### 10.1 车票搜索完整流程

```javascript
// 1. 搜索车票
const searchResponse = await fetch('/api/v1/tickets/search', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    departureCity: '北京',
    arrivalCity: '上海',
    departureDate: '2024-03-15',
    trainType: 'HIGH_SPEED'
  })
});

// 2. 获取车票详情
const detailResponse = await fetch('/api/v1/tickets/G123/detail?date=2024-03-15&departure=BJP&arrival=SHH', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
});

// 3. 创建订单
const orderResponse = await fetch('/api/v1/orders', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    trainNumber: 'G123',
    departureDate: '2024-03-15',
    passengers: [{
      contactId: '12345',
      seatType: 'FIRST',
      seatPosition: 'A'
    }]
  })
});
```

---

## 📚 11. 附录

### 11.1 数据字典

#### 列车类型 (TrainType)
- `HIGH_SPEED`: 高速动车
- `NORMAL`: 普通火车

#### 座位类型 (SeatType)
**高速动车**:
- `BUSINESS`: 商务座
- `FIRST`: 一等座  
- `SECOND`: 二等座

**普通火车**:
- `SOFT_SLEEPER`: 软卧
- `HARD_SLEEPER`: 硬卧
- `SOFT_SEAT`: 软座
- `HARD_SEAT`: 硬座

#### 订单状态 (OrderStatus)
- `PENDING_PAYMENT`: 待支付
- `PAID`: 已支付
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消
- `REFUNDED`: 已退票

#### 支付方式 (PayMethod)
- `WECHAT_PAY`: 微信支付
- `ALIPAY`: 支付宝
- `BANK_CARD`: 银行卡支付

### 11.2 版本更新记录

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2024-03-15 | 初始版本，包含核心功能接口 |

---

**文档维护**: 开发团队  
**最后更新**: 2024-03-15  
**联系方式**: api-support@railway-system.com