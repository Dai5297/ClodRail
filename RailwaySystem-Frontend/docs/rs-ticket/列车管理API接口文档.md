# 列车管理API接口文档

## 概述

列车管理模块提供列车信息的增删改查、列车类型管理、列车状态管理等功能的API接口。基于Train实体类设计，支持列车的完整生命周期管理。

## 基础信息

- **服务名称**: rs-ticket
- **基础路径**: `/api/v1/train`
- **认证方式**: Bearer Token
- **数据格式**: JSON

## 数据模型

### 列车实体 (Train)
```json
{
  "id": "列车ID (Long)",
  "name": "列车名称 (String)",
  "code": "列车编号 (String)",
  "type": "列车类型 (String: G/D/C/K等)",
  "status": "列车状态 (Integer: 0-停运, 1-正常运行, 2-维护中)",
  "createBy": "创建人ID (Long)",
  "createTime": "创建时间 (LocalDateTime)",
  "updateBy": "更新人ID (Long)",
  "updateTime": "更新时间 (LocalDateTime)"
}
```

### 列车类型说明
- **G**: 高速动车组列车 (时速250-350km/h)
- **D**: 动车组列车 (时速200-250km/h)
- **C**: 城际动车组列车 (时速160-200km/h)
- **K**: 快速列车 (时速120-160km/h)
- **T**: 特快列车 (时速140km/h)
- **Z**: 直达特快列车 (时速160km/h)

## API接口

### 1. 列车查询

#### 1.1 查询列车列表
**接口地址**: `GET /api/v1/train/list`

**请求参数**:
```json
{
  "name": "列车名称 (String, 可选, 支持模糊查询)",
  "code": "列车编号 (String, 可选, 支持模糊查询)",
  "type": "列车类型 (String, 可选: G/D/C/K/T/Z)",
  "status": "列车状态 (Integer, 可选: 0-停运, 1-正常运行, 2-维护中)",
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
    "total": 200,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 20,
    "list": [
      {
        "id": 1,
        "name": "复兴号",
        "code": "G1234",
        "type": "G",
        "typeName": "高速动车组列车",
        "status": 1,
        "statusName": "正常运行",
        "createTime": "2024-01-01T00:00:00",
        "updateTime": "2024-01-15T10:30:00"
      },
      {
        "id": 2,
        "name": "和谐号",
        "code": "D5678",
        "type": "D",
        "typeName": "动车组列车",
        "status": 1,
        "statusName": "正常运行",
        "createTime": "2024-01-02T00:00:00",
        "updateTime": "2024-01-15T11:00:00"
      }
    ]
  }
}
```

#### 1.2 获取列车详情
**接口地址**: `GET /api/v1/train/{trainId}`

**路径参数**:
- `trainId`: 列车ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "name": "复兴号",
    "code": "G1234",
    "type": "G",
    "typeName": "高速动车组列车",
    "status": 1,
    "statusName": "正常运行",
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

#### 1.3 根据列车编号查询
**接口地址**: `GET /api/v1/train/code/{trainCode}`

**路径参数**:
- `trainCode`: 列车编号 (String)

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "name": "复兴号",
    "code": "G1234",
    "type": "G",
    "status": 1
  }
}
```

#### 1.4 按类型查询列车
**接口地址**: `GET /api/v1/train/type/{trainType}`

**路径参数**:
- `trainType`: 列车类型 (String: G/D/C/K/T/Z)

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
    "total": 50,
    "list": [
      {
        "id": 1,
        "name": "复兴号",
        "code": "G1234",
        "type": "G",
        "status": 1
      }
    ]
  }
}
```

#### 1.5 列车编号搜索
**接口地址**: `GET /api/v1/train/search`

**请求参数**:
```json
{
  "keyword": "搜索关键词 (String, 必填, 支持列车编号和名称)",
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
      "name": "复兴号",
      "code": "G1234",
      "type": "G",
      "status": 1
    },
    {
      "id": 3,
      "name": "复兴号",
      "code": "G1235",
      "type": "G",
      "status": 1
    }
  ]
}
```

### 2. 列车管理 (管理员接口)

#### 2.1 创建列车
**接口地址**: `POST /api/v1/train/admin`

**请求参数**:
```json
{
  "name": "列车名称 (String, 必填)",
  "code": "列车编号 (String, 必填, 唯一)",
  "type": "列车类型 (String, 必填: G/D/C/K/T/Z)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "列车创建成功",
  "data": {
    "id": 101,
    "name": "复兴号",
    "code": "G9999",
    "type": "G",
    "status": 1
  }
}
```

#### 2.2 更新列车信息
**接口地址**: `PUT /api/v1/train/admin/{trainId}`

**路径参数**:
- `trainId`: 列车ID (Long)

**请求参数**:
```json
{
  "name": "列车名称 (String, 可选)",
  "type": "列车类型 (String, 可选: G/D/C/K/T/Z)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "列车信息更新成功"
}
```

#### 2.3 更新列车状态
**接口地址**: `PUT /api/v1/train/admin/{trainId}/status`

**路径参数**:
- `trainId`: 列车ID (Long)

**请求参数**:
```json
{
  "status": "列车状态 (Integer: 0-停运, 1-正常运行, 2-维护中)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "列车状态更新成功"
}
```

#### 2.4 批量更新列车状态
**接口地址**: `PUT /api/v1/train/admin/batch/status`

**请求参数**:
```json
{
  "trainIds": "列车ID列表 (List<Long>, 必填)",
  "status": "列车状态 (Integer: 0-停运, 1-正常运行, 2-维护中)"
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "批量状态更新成功",
  "data": {
    "successCount": 5,
    "failCount": 0
  }
}
```

#### 2.5 删除列车
**接口地址**: `DELETE /api/v1/train/admin/{trainId}`

**路径参数**:
- `trainId`: 列车ID (Long)

**响应数据**:
```json
{
  "code": 200,
  "message": "列车删除成功"
}
```

### 3. 统计分析

#### 3.1 获取列车统计信息
**接口地址**: `GET /api/v1/train/admin/statistics`

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "totalTrains": 500,
    "runningTrains": 450,
    "stoppedTrains": 30,
    "maintenanceTrains": 20,
    "typeDistribution": [
      {
        "type": "G",
        "typeName": "高速动车组列车",
        "count": 200,
        "runningCount": 180
      },
      {
        "type": "D",
        "typeName": "动车组列车",
        "count": 150,
        "runningCount": 140
      },
      {
        "type": "C",
        "typeName": "城际动车组列车",
        "count": 80,
        "runningCount": 75
      },
      {
        "type": "K",
        "typeName": "快速列车",
        "count": 70,
        "runningCount": 55
      }
    ]
  }
}
```

#### 3.2 按类型统计列车
**接口地址**: `GET /api/v1/train/statistics/type`

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "type": "G",
      "typeName": "高速动车组列车",
      "totalCount": 200,
      "runningCount": 180,
      "stoppedCount": 15,
      "maintenanceCount": 5
    },
    {
      "type": "D",
      "typeName": "动车组列车",
      "totalCount": 150,
      "runningCount": 140,
      "stoppedCount": 8,
      "maintenanceCount": 2
    }
  ]
}
```

### 4. 列车类型管理

#### 4.1 获取所有列车类型
**接口地址**: `GET /api/v1/train/types`

**响应数据**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "type": "G",
      "typeName": "高速动车组列车",
      "description": "时速250-350km/h的高速列车",
      "maxSpeed": 350
    },
    {
      "type": "D",
      "typeName": "动车组列车",
      "description": "时速200-250km/h的动车组",
      "maxSpeed": 250
    },
    {
      "type": "C",
      "typeName": "城际动车组列车",
      "description": "时速160-200km/h的城际列车",
      "maxSpeed": 200
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
| 404 | 列车不存在 |
| 409 | 列车编号已存在 |
| 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 30001 | 列车不存在 |
| 30002 | 列车编号已存在 |
| 30003 | 列车类型不支持 |
| 30004 | 列车状态异常 |
| 30005 | 列车正在使用中，无法删除 |
| 30006 | 列车编号格式错误 |
| 30007 | 列车名称不能为空 |
| 30008 | 列车正在维护中，无法运行 |
| 30009 | 列车已停运，无法执行操作 |
| 30010 | 批量操作部分失败 |

## 注意事项

1. **认证授权**: 管理员接口需要管理员权限，查询接口支持匿名访问
2. **数据唯一性**: 列车编号(code)必须全局唯一
3. **状态管理**: 
   - 停运状态的列车不会出现在车票查询中
   - 维护中的列车暂停售票但保留已售车票
4. **编号规范**: 列车编号需符合铁路部门规范
5. **类型限制**: 列车类型仅支持预定义的几种类型
6. **级联影响**: 删除列车前需确保没有关联的车票
7. **批量操作**: 支持批量状态更新，提高管理效率

## 版本历史

- **v1.0.0** (2024-01-15): 初始版本，包含列车的基础管理功能