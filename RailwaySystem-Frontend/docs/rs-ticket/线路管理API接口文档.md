# 线路管理API接口文档

## 概述

线路管理模块提供铁路线路信息的增删改查、线路状态管理、线路统计等功能的API接口。基于Line实体类设计，支持线路的完整生命周期管理和运营分析。

## 基础信息

- **服务名称**: rs-ticket
- **基础路径**: `/api/v1/line`
- **认证方式**: Bearer Token
- **数据格式**: JSON

## 数据模型

### 线路实体 (Line)
```json
{
  "id": "线路ID (Long)",
  "name": "线路名称 (String)",
  "code": "线路编码 (String)",
  "startStationId": "起始站ID (Long)",
  "endStationId": "终点站ID (Long)",
  "distance": "线路总距离 (Double, 单位:公里)",
  "estimatedTime": "预计行程时间 (Integer, 单位:分钟)",
  "status": "线路状态 (Integer: 0-停运, 1-正常运营, 2-维护中, 3-建设中)",
  "lineType": "线路类型 (String: HIGH_SPEED-高铁, NORMAL-普通铁路, URBAN-城际)",
  "maxSpeed": "最高运行速度 (Integer, 单位:km/h)",
  "createBy": "创建人ID (Long)",
  "createTime": "创建时间 (LocalDateTime)",
  "updateBy": "更新人ID (Long)",
  "updateTime": "更新时间 (LocalDateTime)"
}
```

### 线路详情视图 (LineDetailVO)
```json
{
  "id": "线路ID (Long)",
  "name": "线路名称 (String)",
  "code": "线路编码 (String)",
  "startStation": {
    "id": "起始站ID (Long)",
    "name": "起始站名称 (String)",
    "code": "起始站编码 (String)",
    "cityName": "起始站城市 (String)"
  },
  "endStation": {
    "id": "终点站ID (Long)",
    "name": "终点站名称 (String)",
    "code": "终点站编码 (String)",
    "cityName": "终点站城市 (String)"
  },
  "distance": "线路总距离 (Double)",
  "estimatedTime": "预计行程时间 (Integer)",
  "status": "线路状态 (Integer)",
  "statusName": "线路状态名称 (String)",
  "lineType": "线路类型 (String)",
  "lineTypeName": "线路类型名称 (String)",
  "maxSpeed": "最高运行速度 (Integer)",
  "createTime": "创建时间 (LocalDateTime)",
  "updateTime": "更新时间 (LocalDateTime)"
}
```

### 线路类型说明
- **HIGH_SPEED**: 高速铁路 (时速≥250km/h)
- **NORMAL**: 普通铁路 (时速<250km/h)
- **URBAN**: 城际铁路 (城市间短距离)

## API接口

### 1. 线路查询

#### 1.1 查询线路列表
**接口地址**: `GET /api/v1/line/list`

**请求参数**:
```json
{
  "name": "线路名称 (String, 可选, 支持模糊查询)",
  "code": "线路编码 (String, 可选, 支持模糊查询)",
  "startStationId": "起始站ID (Long, 可选)",
  "endStationId": "终点站ID (Long, 可选)",
  "lineType": "线路类型 (String, 可选: HIGH_SPEED/NORMAL/URBAN)",
  "status": "线路状态 (Integer, 可选: 0-停运, 1-正常运营, 2-维护中, 3-建设中)",
  "minDistance": "最小距离 (Double, 可选, 单位:公里)",
  "maxDistance": "最大距离 (Double, 可选, 单位:公里)",
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
    "total": 150,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 15,
    "list": [
      {
        "id": 1,
        "name": "京沪高铁",
        "code": "G001",
        "startStation": {
          "id": 1,
          "name": "北京南站",
          "code": "VNP",
          "cityName": "北京"
        },
        "endStation": {
          "id": 2,
          "name": "上海虹桥站",
          "code": "AOH",
          "cityName": "上海"
        },
        "distance": 1318.0,
        "estimatedTime": 330,
        "status": 1,
        "statusName": "正常运营",
        "lineType": "HIGH_SPEED",
        "lineTypeName": "高速铁路",
        "maxSpeed": 350,
        "createTime": "2024-01-01T00:00:00",
        "updateTime": "2024-01-15T10:30:00"
      }
    ]
  }
}
```

#### 1.2 获取线路详情
**接口地址**: `GET /api/v1/line/{lineId}`

**路径参数**:
- `lineId`: 线路ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "name": "京沪高铁",
    "code": "G001",
    "startStation": {
      "id": 1,
      "name": "北京南站",
      "code": "VNP",
      "cityName": "北京"
    },
    "endStation": {
      "id": 2,
      "name": "上海虹桥站",
      "code": "AOH",
      "cityName": "上海"
    },
    "distance": 1318.0,
    "estimatedTime": 330,
    "status": 1,
    "statusName": "正常运营",
    "lineType": "HIGH_SPEED",
    "lineTypeName": "高速铁路",
    "maxSpeed": 350,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

#### 1.3 根据线路编码查询
**接口地址**: `GET /api/v1/line/code/{lineCode}`

**路径参数**:
- `lineCode`: 线路编码 (String)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "name": "京沪高铁",
    "code": "G001",
    "startStationId": 1,
    "endStationId": 2,
    "distance": 1318.0,
    "status": 1
  }
}
```

#### 1.4 按起终点站查询线路
**接口地址**: `GET /api/v1/line/route`

**请求参数**:
```json
{
  "startStationId": "起始站ID (Long, 必填)",
  "endStationId": "终点站ID (Long, 必填)",
  "status": "线路状态 (Integer, 可选: 1-仅查询正常运营的线路)"
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
      "name": "京沪高铁",
      "code": "G001",
      "distance": 1318.0,
      "estimatedTime": 330,
      "lineType": "HIGH_SPEED",
      "maxSpeed": 350
    },
    {
      "id": 5,
      "name": "京沪普速",
      "code": "K001",
      "distance": 1463.0,
      "estimatedTime": 720,
      "lineType": "NORMAL",
      "maxSpeed": 160
    }
  ]
}
```

#### 1.5 按类型查询线路
**接口地址**: `GET /api/v1/line/type/{lineType}`

**路径参数**:
- `lineType`: 线路类型 (String: HIGH_SPEED/NORMAL/URBAN)

**请求参数**:
```json
{
  "status": "线路状态 (Integer, 可选: 1-仅查询正常运营的线路)",
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
    "total": 80,
    "list": [
      {
        "id": 1,
        "name": "京沪高铁",
        "code": "G001",
        "distance": 1318.0,
        "lineType": "HIGH_SPEED",
        "status": 1
      }
    ]
  }
}
```

#### 1.6 线路搜索
**接口地址**: `GET /api/v1/line/search`

**请求参数**:
```json
{
  "keyword": "搜索关键词 (String, 必填, 支持线路名称和编码)",
  "limit": "返回数量限制 (Integer, 默认10)"
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
      "name": "京沪高铁",
      "code": "G001",
      "startStationName": "北京南站",
      "endStationName": "上海虹桥站",
      "lineType": "HIGH_SPEED",
      "status": 1
    }
  ]
}
```

### 2. 线路管理 (管理员接口)

#### 2.1 创建线路
**接口地址**: `POST /api/v1/line/admin`

**请求参数**:
```json
{
  "name": "线路名称 (String, 必填)",
  "code": "线路编码 (String, 必填, 唯一)",
  "startStationId": "起始站ID (Long, 必填)",
  "endStationId": "终点站ID (Long, 必填)",
  "distance": "线路总距离 (Double, 必填, 单位:公里)",
  "estimatedTime": "预计行程时间 (Integer, 必填, 单位:分钟)",
  "lineType": "线路类型 (String, 必填: HIGH_SPEED/NORMAL/URBAN)",
  "maxSpeed": "最高运行速度 (Integer, 必填, 单位:km/h)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "线路创建成功",
  "data": {
    "id": 101,
    "name": "京广高铁",
    "code": "G002",
    "startStationId": 1,
    "endStationId": 10,
    "distance": 2298.0,
    "status": 3
  }
}
```

#### 2.2 更新线路信息
**接口地址**: `PUT /api/v1/line/admin/{lineId}`

**路径参数**:
- `lineId`: 线路ID (Long)

**请求参数**:
```json
{
  "name": "线路名称 (String, 可选)",
  "distance": "线路总距离 (Double, 可选)",
  "estimatedTime": "预计行程时间 (Integer, 可选)",
  "maxSpeed": "最高运行速度 (Integer, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "线路信息更新成功"
}
```

#### 2.3 更新线路状态
**接口地址**: `PUT /api/v1/line/admin/{lineId}/status`

**路径参数**:
- `lineId`: 线路ID (Long)

**请求参数**:
```json
{
  "status": "线路状态 (Integer: 0-停运, 1-正常运营, 2-维护中, 3-建设中)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "线路状态更新成功"
}
```

#### 2.4 批量更新线路状态
**接口地址**: `PUT /api/v1/line/admin/batch/status`

**请求参数**:
```json
{
  "lineIds": "线路ID列表 (List<Long>, 必填)",
  "status": "线路状态 (Integer: 0-停运, 1-正常运营, 2-维护中)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "批量状态更新成功",
  "data": {
    "successCount": 8,
    "failCount": 0
  }
}
```

#### 2.5 删除线路
**接口地址**: `DELETE /api/v1/line/admin/{lineId}`

**路径参数**:
- `lineId`: 线路ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "线路删除成功"
}
```

### 3. 统计分析

#### 3.1 获取线路统计信息
**接口地址**: `GET /api/v1/line/admin/statistics`

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "totalLines": 150,
    "operatingLines": 120,
    "stoppedLines": 10,
    "maintenanceLines": 15,
    "constructionLines": 5,
    "totalDistance": 45000.0,
    "averageDistance": 300.0,
    "typeDistribution": [
      {
        "lineType": "HIGH_SPEED",
        "typeName": "高速铁路",
        "count": 80,
        "operatingCount": 70,
        "totalDistance": 25000.0,
        "averageDistance": 312.5
      },
      {
        "lineType": "NORMAL",
        "typeName": "普通铁路",
        "count": 50,
        "operatingCount": 35,
        "totalDistance": 15000.0,
        "averageDistance": 300.0
      },
      {
        "lineType": "URBAN",
        "typeName": "城际铁路",
        "count": 20,
        "operatingCount": 15,
        "totalDistance": 5000.0,
        "averageDistance": 250.0
      }
    ]
  }
}
```

#### 3.2 按类型统计线路
**接口地址**: `GET /api/v1/line/statistics/type`

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "lineType": "HIGH_SPEED",
      "typeName": "高速铁路",
      "totalCount": 80,
      "operatingCount": 70,
      "stoppedCount": 5,
      "maintenanceCount": 4,
      "constructionCount": 1,
      "totalDistance": 25000.0,
      "averageDistance": 312.5,
      "maxSpeed": 350,
      "minSpeed": 250
    }
  ]
}
```

#### 3.3 按距离区间统计
**接口地址**: `GET /api/v1/line/statistics/distance`

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "distanceRange": "0-100km",
      "count": 20,
      "percentage": 13.3
    },
    {
      "distanceRange": "100-500km",
      "count": 60,
      "percentage": 40.0
    },
    {
      "distanceRange": "500-1000km",
      "count": 45,
      "percentage": 30.0
    },
    {
      "distanceRange": "1000km以上",
      "count": 25,
      "percentage": 16.7
    }
  ]
}
```

### 4. 线路关联查询

#### 4.1 获取线路经过的车站
**接口地址**: `GET /api/v1/line/{lineId}/stations`

**路径参数**:
- `lineId`: 线路ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "stationId": 1,
      "stationName": "北京南站",
      "stationCode": "VNP",
      "cityName": "北京",
      "sequence": 1,
      "arrivalTime": null,
      "departureTime": "08:00:00",
      "stopDuration": 0
    },
    {
      "stationId": 5,
      "stationName": "济南西站",
      "stationCode": "JGK",
      "cityName": "济南",
      "sequence": 2,
      "arrivalTime": "09:30:00",
      "departureTime": "09:32:00",
      "stopDuration": 2
    },
    {
      "stationId": 2,
      "stationName": "上海虹桥站",
      "stationCode": "AOH",
      "cityName": "上海",
      "sequence": 3,
      "arrivalTime": "13:30:00",
      "departureTime": null,
      "stopDuration": 0
    }
  ]
}
```

#### 4.2 获取线路上运行的列车
**接口地址**: `GET /api/v1/line/{lineId}/trains`

**路径参数**:
- `lineId`: 线路ID (Long)

**请求参数**:
```json
{
  "status": "列车状态 (Integer, 可选: 1-仅查询正常运行的列车)",
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
    "total": 25,
    "list": [
      {
        "trainId": 1,
        "trainName": "复兴号",
        "trainCode": "G1234",
        "trainType": "G",
        "status": 1,
        "departureTime": "08:00:00",
        "arrivalTime": "13:30:00"
      }
    ]
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
| 404 | 线路不存在 |
| 409 | 线路编码已存在 |
| 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 40001 | 线路不存在 |
| 40002 | 线路编码已存在 |
| 40003 | 线路类型不支持 |
| 40004 | 线路状态异常 |
| 40005 | 线路正在使用中，无法删除 |
| 40006 | 起始站和终点站不能相同 |
| 40007 | 车站不存在 |
| 40008 | 线路距离必须大于0 |
| 40009 | 预计时间必须大于0 |
| 40010 | 最高速度设置不合理 |
| 40011 | 线路正在维护中，无法运营 |
| 40012 | 线路已停运，无法执行操作 |
| 40013 | 线路还在建设中，无法投入使用 |
| 40014 | 批量操作部分失败 |
| 40015 | 线路名称不能为空 |

## 注意事项

1. **认证授权**: 管理员接口需要管理员权限，查询接口支持匿名访问
2. **数据唯一性**: 线路编码(code)必须全局唯一
3. **状态管理**: 
   - 停运状态的线路不会出现在车票查询中
   - 维护中的线路暂停售票但保留已售车票
   - 建设中的线路不能投入运营
4. **数据完整性**: 
   - 起始站和终点站必须存在且不能相同
   - 距离和时间必须为正数
   - 最高速度需符合线路类型规范
5. **级联影响**: 删除线路前需确保没有关联的列车和车票
6. **性能优化**: 支持按多种条件查询和分页
7. **业务规则**: 
   - 高速铁路最高速度≥250km/h
   - 普通铁路最高速度<250km/h
   - 城际铁路通常用于短距离城市间连接

## 版本历史

- **v1.0.0** (2024-01-15): 初始版本，包含线路的基础管理功能
- **v1.1.0** (2024-01-20): 新增线路统计分析功能
- **v1.2.0** (2024-01-25): 新增线路关联查询功能