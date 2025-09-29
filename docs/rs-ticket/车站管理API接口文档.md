# 车站管理API接口文档

## 概述

车站管理模块提供车站信息的增删改查、热门车站查询、城市车站查询等功能的API接口。基于Station实体类设计，支持车站的完整生命周期管理。

## 基础信息

- **服务名称**: rs-ticket
- **基础路径**: `/api/v1/station`
- **认证方式**: Bearer Token
- **数据格式**: JSON

## 数据模型

### 车站实体 (Station)
```json
{
  "id": "车站ID (Long)",
  "name": "站名 (String)",
  "code": "站码 (String)",
  "address": "地址 (String)",
  "city": "所属城市 (String)",
  "province": "所属省份 (String)",
  "longitude": "经度 (String)",
  "latitude": "纬度 (String)",
  "status": "状态 (Integer: 0-禁用, 1-启用)",
  "isHot": "是否热门 (Integer: 0-否, 1-是)",
  "description": "描述 (String)",
  "phone": "联系电话 (String)",
  "createBy": "创建人ID (Long)",
  "createTime": "创建时间 (LocalDateTime)",
  "updateBy": "更新人ID (Long)",
  "updateTime": "更新时间 (LocalDateTime)"
}
```

## API接口

### 1. 车站查询

#### 1.1 查询车站列表
**接口地址**: `GET /api/v1/station/list`

**请求参数**:
```json
{
  "name": "站名 (String, 可选, 支持模糊查询)",
  "code": "站码 (String, 可选)",
  "city": "城市 (String, 可选)",
  "province": "省份 (String, 可选)",
  "status": "状态 (Integer, 可选: 0-禁用, 1-启用)",
  "isHot": "是否热门 (Integer, 可选: 0-否, 1-是)",
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
        "name": "北京南",
        "code": "VNP",
        "address": "北京市丰台区永外大街车站路12号",
        "city": "北京",
        "province": "北京",
        "longitude": "116.378631",
        "latitude": "39.865195",
        "status": 1,
        "isHot": 1,
        "description": "北京南站是北京市的主要高铁车站",
        "phone": "010-12306",
        "createTime": "2024-01-01T00:00:00",
        "updateTime": "2024-01-15T10:30:00"
      }
    ]
  }
}
```

#### 1.2 获取车站详情
**接口地址**: `GET /api/v1/station/{stationId}`

**路径参数**:
- `stationId`: 车站ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "name": "北京南",
    "code": "VNP",
    "address": "北京市丰台区永外大街车站路12号",
    "city": "北京",
    "province": "北京",
    "longitude": "116.378631",
    "latitude": "39.865195",
    "status": 1,
    "isHot": 1,
    "description": "北京南站是北京市的主要高铁车站",
    "phone": "010-12306",
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

#### 1.3 根据站码查询车站
**接口地址**: `GET /api/v1/station/code/{stationCode}`

**路径参数**:
- `stationCode`: 车站代码 (String)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "name": "北京南",
    "code": "VNP",
    "city": "北京",
    "province": "北京"
  }
}
```

#### 1.4 查询热门车站
**接口地址**: `GET /api/v1/station/hot`

**请求参数**:
```json
{
  "limit": "返回数量限制 (Integer, 默认10, 最大50)"
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
      "name": "北京南",
      "code": "VNP",
      "city": "北京",
      "province": "北京"
    },
    {
      "id": 2,
      "name": "上海虹桥",
      "code": "AOH",
      "city": "上海",
      "province": "上海"
    }
  ]
}
```

#### 1.5 按城市查询车站
**接口地址**: `GET /api/v1/station/city/{cityName}`

**路径参数**:
- `cityName`: 城市名称 (String)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "name": "北京南",
      "code": "VNP",
      "status": 1
    },
    {
      "id": 3,
      "name": "北京西",
      "code": "BXP",
      "status": 1
    }
  ]
}
```

#### 1.6 车站名称模糊搜索
**接口地址**: `GET /api/v1/station/search`

**请求参数**:
```json
{
  "keyword": "搜索关键词 (String, 必填)",
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
      "name": "北京南",
      "code": "VNP",
      "city": "北京",
      "province": "北京"
    },
    {
      "id": 3,
      "name": "北京西",
      "code": "BXP",
      "city": "北京",
      "province": "北京"
    }
  ]
}
```

### 2. 车站管理 (管理员接口)

#### 2.1 创建车站
**接口地址**: `POST /api/v1/station/admin`

**请求参数**:
```json
{
  "name": "站名 (String, 必填)",
  "code": "站码 (String, 必填, 唯一)",
  "address": "地址 (String, 可选)",
  "city": "所属城市 (String, 必填)",
  "province": "所属省份 (String, 必填)",
  "longitude": "经度 (String, 可选)",
  "latitude": "纬度 (String, 可选)",
  "description": "描述 (String, 可选)",
  "phone": "联系电话 (String, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "车站创建成功",
  "data": {
    "id": 101,
    "name": "深圳北",
    "code": "IOQ",
    "status": 1
  }
}
```

#### 2.2 更新车站信息
**接口地址**: `PUT /api/v1/station/admin/{stationId}`

**路径参数**:
- `stationId`: 车站ID (Long)

**请求参数**:
```json
{
  "name": "站名 (String, 可选)",
  "address": "地址 (String, 可选)",
  "city": "所属城市 (String, 可选)",
  "province": "所属省份 (String, 可选)",
  "longitude": "经度 (String, 可选)",
  "latitude": "纬度 (String, 可选)",
  "description": "描述 (String, 可选)",
  "phone": "联系电话 (String, 可选)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "车站信息更新成功"
}
```

#### 2.3 更新车站状态
**接口地址**: `PUT /api/v1/station/admin/{stationId}/status`

**路径参数**:
- `stationId`: 车站ID (Long)

**请求参数**:
```json
{
  "status": "状态 (Integer: 0-禁用, 1-启用)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "车站状态更新成功"
}
```

#### 2.4 设置热门车站
**接口地址**: `PUT /api/v1/station/admin/{stationId}/hot`

**路径参数**:
- `stationId`: 车站ID (Long)

**请求参数**:
```json
{
  "isHot": "是否热门 (Integer: 0-否, 1-是)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "热门状态设置成功"
}
```

#### 2.5 删除车站
**接口地址**: `DELETE /api/v1/station/admin/{stationId}`

**路径参数**:
- `stationId`: 车站ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "车站删除成功"
}
```

### 3. 统计分析

#### 3.1 获取车站统计信息
**接口地址**: `GET /api/v1/station/admin/statistics`

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "totalStations": 350,
    "activeStations": 340,
    "inactiveStations": 10,
    "hotStations": 25,
    "provinceDistribution": [
      {
        "province": "北京",
        "count": 15
      },
      {
        "province": "上海", 
        "count": 12
      }
    ],
    "cityDistribution": [
      {
        "city": "北京",
        "count": 15
      },
      {
        "city": "上海",
        "count": 12
      }
    ]
  }
}
```

#### 3.2 按省份统计车站
**接口地址**: `GET /api/v1/station/statistics/province`

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "province": "北京",
      "totalCount": 15,
      "activeCount": 15,
      "hotCount": 3
    },
    {
      "province": "上海",
      "totalCount": 12,
      "activeCount": 12,
      "hotCount": 2
    }
  ]
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 车站不存在 |
| 409 | 车站代码已存在 |
| 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 20001 | 车站不存在 |
| 20002 | 车站代码已存在 |
| 20003 | 车站名称已存在 |
| 20004 | 车站状态异常 |
| 20005 | 车站正在使用中，无法删除 |
| 20006 | 经纬度格式错误 |
| 20007 | 电话号码格式错误 |
| 20008 | 城市名称不能为空 |
| 20009 | 省份名称不能为空 |
| 20010 | 车站代码格式错误 |

## 注意事项

1. **认证授权**: 管理员接口需要管理员权限，查询接口支持匿名访问
2. **数据唯一性**: 车站代码(code)必须全局唯一
3. **状态管理**: 禁用的车站不会出现在车票查询结果中
4. **热门车站**: 热门车站会在首页和搜索结果中优先显示
5. **地理坐标**: 经纬度用于地图显示和距离计算
6. **级联删除**: 删除车站前需确保没有关联的线路和车票
7. **缓存策略**: 热门车站和城市车站列表会进行缓存优化

## 版本历史

- **v1.0.0** (2024-01-15): 初始版本，包含车站的基础管理功能