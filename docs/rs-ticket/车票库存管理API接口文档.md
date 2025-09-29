# 车票库存管理API接口文档

## 概述

车票库存管理模块提供车票库存的查询、更新、统计分析等功能的API接口。基于TicketInventory实体类设计，支持实时库存管理、座位类型管理和销售统计分析。

## 基础信息

- **服务名称**: rs-ticket
- **基础路径**: `/api/v1/inventory`
- **认证方式**: Bearer Token
- **数据格式**: JSON

## 数据模型

### 车票库存实体 (TicketInventory)
```json
{
  "id": "库存ID (Long)",
  "trainId": "列车ID (Long)",
  "lineId": "线路ID (Long)",
  "departureDate": "发车日期 (LocalDate)",
  "seatType": "座位类型 (String: BUSINESS-商务座, FIRST-一等座, SECOND-二等座, HARD_SEAT-硬座, SOFT_SEAT-软座, HARD_SLEEPER-硬卧, SOFT_SLEEPER-软卧)",
  "totalSeats": "总座位数 (Integer)",
  "availableSeats": "可用座位数 (Integer)",
  "soldSeats": "已售座位数 (Integer)",
  "price": "票价 (BigDecimal)",
  "status": "库存状态 (Integer: 0-停售, 1-正常售票, 2-预售, 3-售罄)",
  "createBy": "创建人ID (Long)",
  "createTime": "创建时间 (LocalDateTime)",
  "updateBy": "更新人ID (Long)",
  "updateTime": "更新时间 (LocalDateTime)"
}
```

### 库存详情视图 (InventoryDetailVO)
```json
{
  "id": "库存ID (Long)",
  "train": {
    "id": "列车ID (Long)",
    "name": "列车名称 (String)",
    "code": "列车编号 (String)",
    "type": "列车类型 (String)"
  },
  "line": {
    "id": "线路ID (Long)",
    "name": "线路名称 (String)",
    "code": "线路编码 (String)",
    "startStationName": "起始站名称 (String)",
    "endStationName": "终点站名称 (String)",
    "distance": "线路距离 (Double)"
  },
  "departureDate": "发车日期 (LocalDate)",
  "seatType": "座位类型 (String)",
  "seatTypeName": "座位类型名称 (String)",
  "totalSeats": "总座位数 (Integer)",
  "availableSeats": "可用座位数 (Integer)",
  "soldSeats": "已售座位数 (Integer)",
  "occupancyRate": "上座率 (Double, 百分比)",
  "price": "票价 (BigDecimal)",
  "status": "库存状态 (Integer)",
  "statusName": "库存状态名称 (String)",
  "createTime": "创建时间 (LocalDateTime)",
  "updateTime": "更新时间 (LocalDateTime)"
}
```

### 座位类型说明
- **BUSINESS**: 商务座 (最高级别座位)
- **FIRST**: 一等座 (高级座位)
- **SECOND**: 二等座 (标准座位)
- **HARD_SEAT**: 硬座 (普通座位)
- **SOFT_SEAT**: 软座 (舒适座位)
- **HARD_SLEEPER**: 硬卧 (普通卧铺)
- **SOFT_SLEEPER**: 软卧 (高级卧铺)

## API接口

### 1. 库存查询

#### 1.1 查询库存列表
**接口地址**: `GET /api/v1/inventory/list`

**请求参数**:
```json
{
  "trainId": "列车ID (Long, 可选)",
  "lineId": "线路ID (Long, 可选)",
  "departureDate": "发车日期 (LocalDate, 可选, 格式: yyyy-MM-dd)",
  "startDate": "开始日期 (LocalDate, 可选)",
  "endDate": "结束日期 (LocalDate, 可选)",
  "seatType": "座位类型 (String, 可选)",
  "status": "库存状态 (Integer, 可选: 0-停售, 1-正常售票, 2-预售, 3-售罄)",
  "minPrice": "最低价格 (BigDecimal, 可选)",
  "maxPrice": "最高价格 (BigDecimal, 可选)",
  "availableOnly": "仅显示有票 (Boolean, 可选, 默认false)",
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
    "total": 500,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 50,
    "list": [
      {
        "id": 1,
        "train": {
          "id": 1,
          "name": "复兴号",
          "code": "G1234",
          "type": "G"
        },
        "line": {
          "id": 1,
          "name": "京沪高铁",
          "code": "G001",
          "startStationName": "北京南站",
          "endStationName": "上海虹桥站",
          "distance": 1318.0
        },
        "departureDate": "2024-02-01",
        "seatType": "SECOND",
        "seatTypeName": "二等座",
        "totalSeats": 556,
        "availableSeats": 123,
        "soldSeats": 433,
        "occupancyRate": 77.88,
        "price": 553.50,
        "status": 1,
        "statusName": "正常售票",
        "createTime": "2024-01-15T10:00:00",
        "updateTime": "2024-01-15T15:30:00"
      }
    ]
  }
}
```

#### 1.2 获取库存详情
**接口地址**: `GET /api/v1/inventory/{inventoryId}`

**路径参数**:
- `inventoryId`: 库存ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "train": {
      "id": 1,
      "name": "复兴号",
      "code": "G1234",
      "type": "G"
    },
    "line": {
      "id": 1,
      "name": "京沪高铁",
      "code": "G001",
      "startStationName": "北京南站",
      "endStationName": "上海虹桥站",
      "distance": 1318.0
    },
    "departureDate": "2024-02-01",
    "seatType": "SECOND",
    "seatTypeName": "二等座",
    "totalSeats": 556,
    "availableSeats": 123,
    "soldSeats": 433,
    "occupancyRate": 77.88,
    "price": 553.50,
    "status": 1,
    "statusName": "正常售票",
    "createTime": "2024-01-15T10:00:00",
    "updateTime": "2024-01-15T15:30:00"
  }
}
```

#### 1.3 按车次和日期查询库存
**接口地址**: `GET /api/v1/inventory/train/{trainId}/date/{departureDate}`

**路径参数**:
- `trainId`: 列车ID (Long)
- `departureDate`: 发车日期 (String, 格式: yyyy-MM-dd)

**请求参数**:
```json
{
  "availableOnly": "仅显示有票 (Boolean, 可选, 默认false)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "seatType": "BUSINESS",
      "seatTypeName": "商务座",
      "totalSeats": 24,
      "availableSeats": 8,
      "soldSeats": 16,
      "occupancyRate": 66.67,
      "price": 1748.00,
      "status": 1
    },
    {
      "id": 2,
      "seatType": "FIRST",
      "seatTypeName": "一等座",
      "totalSeats": 85,
      "availableSeats": 25,
      "soldSeats": 60,
      "occupancyRate": 70.59,
      "price": 933.00,
      "status": 1
    },
    {
      "id": 3,
      "seatType": "SECOND",
      "seatTypeName": "二等座",
      "totalSeats": 556,
      "availableSeats": 123,
      "soldSeats": 433,
      "occupancyRate": 77.88,
      "price": 553.50,
      "status": 1
    }
  ]
}
```

#### 1.4 检查库存可用性
**接口地址**: `GET /api/v1/inventory/check`

**请求参数**:
```json
{
  "trainId": "列车ID (Long, 必填)",
  "departureDate": "发车日期 (LocalDate, 必填)",
  "seatType": "座位类型 (String, 必填)",
  "quantity": "需要座位数 (Integer, 必填)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "available": true,
    "inventoryId": 1,
    "availableSeats": 123,
    "requestedSeats": 2,
    "price": 553.50,
    "totalPrice": 1107.00
  }
}
```

### 2. 库存管理 (管理员接口)

#### 2.1 创建库存
**接口地址**: `POST /api/v1/inventory/admin`

**请求参数**:
```json
{
  "trainId": "列车ID (Long, 必填)",
  "lineId": "线路ID (Long, 必填)",
  "departureDate": "发车日期 (LocalDate, 必填)",
  "seatType": "座位类型 (String, 必填)",
  "totalSeats": "总座位数 (Integer, 必填)",
  "price": "票价 (BigDecimal, 必填)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "库存创建成功",
  "data": {
    "id": 101,
    "trainId": 1,
    "lineId": 1,
    "departureDate": "2024-02-15",
    "seatType": "SECOND",
    "totalSeats": 556,
    "availableSeats": 556,
    "price": 553.50,
    "status": 2
  }
}
```

#### 2.2 批量创建库存
**接口地址**: `POST /api/v1/inventory/admin/batch`

**请求参数**:
```json
{
  "trainId": "列车ID (Long, 必填)",
  "lineId": "线路ID (Long, 必填)",
  "startDate": "开始日期 (LocalDate, 必填)",
  "endDate": "结束日期 (LocalDate, 必填)",
  "seatConfigs": [
    {
      "seatType": "座位类型 (String, 必填)",
      "totalSeats": "总座位数 (Integer, 必填)",
      "price": "票价 (BigDecimal, 必填)"
    }
  ]
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "批量库存创建成功",
  "data": {
    "totalCreated": 90,
    "dateRange": "2024-02-01 至 2024-02-29",
    "seatTypes": ["BUSINESS", "FIRST", "SECOND"]
  }
}
```

#### 2.3 更新库存信息
**接口地址**: `PUT /api/v1/inventory/admin/{inventoryId}`

**路径参数**:
- `inventoryId`: 库存ID (Long)

**请求参数**:
```json
{
  "totalSeats": "总座位数 (Integer, 可选)",
  "price": "票价 (BigDecimal, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "库存信息更新成功"
}
```

#### 2.4 更新库存状态
**接口地址**: `PUT /api/v1/inventory/admin/{inventoryId}/status`

**路径参数**:
- `inventoryId`: 库存ID (Long)

**请求参数**:
```json
{
  "status": "库存状态 (Integer: 0-停售, 1-正常售票, 2-预售, 3-售罄)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "库存状态更新成功"
}
```

#### 2.5 调整库存数量
**接口地址**: `PUT /api/v1/inventory/admin/{inventoryId}/adjust`

**路径参数**:
- `inventoryId`: 库存ID (Long)

**请求参数**:
```json
{
  "adjustType": "调整类型 (String: ADD-增加, REDUCE-减少, SET-设置)",
  "quantity": "调整数量 (Integer, 必填)",
  "reason": "调整原因 (String, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "库存调整成功",
  "data": {
    "beforeAdjust": 123,
    "afterAdjust": 133,
    "adjustQuantity": 10,
    "adjustType": "ADD"
  }
}
```

#### 2.6 批量调整价格
**接口地址**: `PUT /api/v1/inventory/admin/batch/price`

**请求参数**:
```json
{
  "trainId": "列车ID (Long, 可选)",
  "lineId": "线路ID (Long, 可选)",
  "seatType": "座位类型 (String, 可选)",
  "startDate": "开始日期 (LocalDate, 可选)",
  "endDate": "结束日期 (LocalDate, 可选)",
  "adjustType": "调整类型 (String: PERCENTAGE-按百分比, AMOUNT-按金额)",
  "adjustValue": "调整值 (BigDecimal, 必填)",
  "reason": "调整原因 (String, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "批量价格调整成功",
  "data": {
    "affectedCount": 150,
    "adjustType": "PERCENTAGE",
    "adjustValue": 10.0,
    "reason": "春运涨价"
  }
}
```

### 3. 库存统计

#### 3.1 获取库存统计概览
**接口地址**: `GET /api/v1/inventory/admin/statistics/overview`

**请求参数**:
```json
{
  "startDate": "开始日期 (LocalDate, 可选)",
  "endDate": "结束日期 (LocalDate, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "totalInventories": 5000,
    "totalSeats": 2500000,
    "totalSoldSeats": 1875000,
    "totalAvailableSeats": 625000,
    "overallOccupancyRate": 75.0,
    "totalRevenue": 1250000000.00,
    "averagePrice": 666.67,
    "statusDistribution": [
      {
        "status": 1,
        "statusName": "正常售票",
        "count": 4200,
        "percentage": 84.0
      },
      {
        "status": 3,
        "statusName": "售罄",
        "count": 600,
        "percentage": 12.0
      },
      {
        "status": 0,
        "statusName": "停售",
        "count": 150,
        "percentage": 3.0
      },
      {
        "status": 2,
        "statusName": "预售",
        "count": 50,
        "percentage": 1.0
      }
    ]
  }
}
```

#### 3.2 按座位类型统计
**接口地址**: `GET /api/v1/inventory/statistics/seat-type`

**请求参数**:
```json
{
  "startDate": "开始日期 (LocalDate, 可选)",
  "endDate": "结束日期 (LocalDate, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "seatType": "SECOND",
      "seatTypeName": "二等座",
      "totalSeats": 1500000,
      "soldSeats": 1200000,
      "availableSeats": 300000,
      "occupancyRate": 80.0,
      "averagePrice": 450.00,
      "totalRevenue": 540000000.00,
      "inventoryCount": 3000
    },
    {
      "seatType": "FIRST",
      "seatTypeName": "一等座",
      "totalSeats": 600000,
      "soldSeats": 420000,
      "availableSeats": 180000,
      "occupancyRate": 70.0,
      "averagePrice": 750.00,
      "totalRevenue": 315000000.00,
      "inventoryCount": 1500
    }
  ]
}
```

#### 3.3 按日期统计销售情况
**接口地址**: `GET /api/v1/inventory/statistics/daily`

**请求参数**:
```json
{
  "startDate": "开始日期 (LocalDate, 必填)",
  "endDate": "结束日期 (LocalDate, 必填)",
  "trainId": "列车ID (Long, 可选)",
  "lineId": "线路ID (Long, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "date": "2024-02-01",
      "totalSeats": 50000,
      "soldSeats": 42000,
      "availableSeats": 8000,
      "occupancyRate": 84.0,
      "revenue": 25200000.00,
      "averagePrice": 600.00,
      "inventoryCount": 100
    },
    {
      "date": "2024-02-02",
      "totalSeats": 52000,
      "soldSeats": 39000,
      "availableSeats": 13000,
      "occupancyRate": 75.0,
      "revenue": 23400000.00,
      "averagePrice": 600.00,
      "inventoryCount": 104
    }
  ]
}
```

#### 3.4 热门线路统计
**接口地址**: `GET /api/v1/inventory/statistics/popular-lines`

**请求参数**:
```json
{
  "startDate": "开始日期 (LocalDate, 可选)",
  "endDate": "结束日期 (LocalDate, 可选)",
  "limit": "返回数量 (Integer, 默认10)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "lineId": 1,
      "lineName": "京沪高铁",
      "lineCode": "G001",
      "totalSeats": 500000,
      "soldSeats": 450000,
      "occupancyRate": 90.0,
      "revenue": 270000000.00,
      "averagePrice": 600.00,
      "trainCount": 50,
      "inventoryCount": 1500
    }
  ]
}
```

### 4. 实时库存操作

#### 4.1 预占座位
**接口地址**: `POST /api/v1/inventory/{inventoryId}/reserve`

**路径参数**:
- `inventoryId`: 库存ID (Long)

**请求参数**:
```json
{
  "quantity": "预占数量 (Integer, 必填)",
  "reserveTime": "预占时长 (Integer, 可选, 单位:分钟, 默认15)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "座位预占成功",
  "data": {
    "reserveId": "R20240115001",
    "inventoryId": 1,
    "quantity": 2,
    "expireTime": "2024-01-15T16:15:00",
    "remainingSeats": 121
  }
}
```

#### 4.2 确认购买
**接口地址**: `POST /api/v1/inventory/confirm`

**请求参数**:
```json
{
  "reserveId": "预占ID (String, 必填)",
  "quantity": "确认购买数量 (Integer, 必填)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "购买确认成功",
  "data": {
    "inventoryId": 1,
    "soldQuantity": 2,
    "remainingSeats": 121,
    "totalPrice": 1107.00
  }
}
```

#### 4.3 释放预占
**接口地址**: `POST /api/v1/inventory/release`

**请求参数**:
```json
{
  "reserveId": "预占ID (String, 必填)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "预占释放成功",
  "data": {
    "inventoryId": 1,
    "releasedQuantity": 2,
    "remainingSeats": 123
  }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 库存不存在 |
| 409 | 库存冲突 |
| 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 50001 | 库存不存在 |
| 50002 | 库存已存在 |
| 50003 | 座位类型不支持 |
| 50004 | 库存状态异常 |
| 50005 | 库存不足 |
| 50006 | 座位数量必须大于0 |
| 50007 | 票价必须大于0 |
| 50008 | 库存已售罄 |
| 50009 | 库存已停售 |
| 50010 | 预占已过期 |
| 50011 | 预占不存在 |
| 50012 | 预占数量不足 |
| 50013 | 调整数量超出范围 |
| 50014 | 价格调整幅度过大 |
| 50015 | 批量操作部分失败 |
| 50016 | 发车日期不能是过去时间 |
| 50017 | 列车或线路不存在 |
| 50018 | 库存正在使用中，无法删除 |

## 注意事项

1. **认证授权**: 管理员接口需要管理员权限，查询接口支持匿名访问
2. **实时性**: 库存数据实时更新，支持高并发访问
3. **预占机制**: 
   - 预占时间默认15分钟
   - 超时自动释放
   - 支持手动释放
4. **数据一致性**: 
   - 使用乐观锁防止超卖
   - 库存变更记录审计日志
5. **业务规则**: 
   - 总座位数 = 可用座位数 + 已售座位数
   - 上座率 = 已售座位数 / 总座位数 × 100%
6. **性能优化**: 
   - 支持批量操作
   - 缓存热点数据
   - 异步处理统计任务
7. **安全控制**: 
   - 价格调整需要审批
   - 库存调整记录操作人
   - 防止恶意刷票

## 版本历史

- **v1.0.0** (2024-01-15): 初始版本，包含库存的基础管理功能
- **v1.1.0** (2024-01-20): 新增实时预占机制
- **v1.2.0** (2024-01-25): 新增统计分析功能
- **v1.3.0** (2024-01-30): 新增批量操作功能