# 积分商城API接口文档

## 概述

本文档定义了积分商城前端所需的后端API接口规范。所有接口的基础路径为 `/api`。

## 通用说明

### 请求头

所有需要认证的接口都需要在请求头中携带 Token：

```
Authorization: Bearer {token}
```

### 响应格式

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- `code`: 状态码，200表示成功，其他表示失败
- `message`: 返回消息
- `data`: 返回数据

### 错误码

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证或认证失败 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 1. 积分管理相关接口

### 1.1 获取用户积分信息

**接口地址：** `GET /points/info`

**功能描述：** 获取当前登录用户的积分信息

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "currentPoints": 1200,
    "totalEarned": 5000,
    "totalSpent": 3800,
    "expiringSoon": 200
  }
}
```

**字段说明：**

| 字段 | 类型 | 说明 |
|-----|------|------|
| currentPoints | Integer | 当前可用积分 |
| totalEarned | Integer | 累计获得积分 |
| totalSpent | Integer | 累计消费积分 |
| expiringSoon | Integer | 即将过期积分 |

---

### 1.2 获取积分明细

**接口地址：** `GET /points/history`

**功能描述：** 分页获取用户的积分明细记录

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | Integer | 是 | 页码，从1开始 |
| size | Integer | 是 | 每页条数 |
| type | String | 否 | 类型筛选：earn(获得)、spend(消费)、expire(过期) |
| startDate | String | 否 | 开始日期，格式：YYYY-MM-DD |
| endDate | String | 否 | 结束日期，格式：YYYY-MM-DD |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": "1",
        "type": "earn",
        "description": "购买车票获得积分",
        "points": 120,
        "createTime": "2024-01-15T14:30:00"
      }
    ],
    "total": 100
  }
}
```

---

## 2. 积分商城相关接口

### 2.1 获取商品列表

**接口地址：** `GET /points-mall/products`

**功能描述：** 分页获取积分商城商品列表

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | Integer | 是 | 页码，从1开始 |
| size | Integer | 是 | 每页条数 |
| category | String | 否 | 商品分类：coupons、travel、electronics、lifestyle、food |
| keyword | String | 否 | 搜索关键词 |
| sortBy | String | 否 | 排序方式：default、points-asc、points-desc、popularity |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "products": [
      {
        "id": 100002672305,
        "name": "10元车票优惠券",
        "image": "http://example.com/image1.jpg",
        "images": null,
        "price": 500,
        "stock": 1000,
        "category": "coupons",
        "brand": "品牌A",
        "spec": "有效期30天，使用范围全国通用",
        "description": "可用于购买任意车票抵打10元",
        "sold": 1500,
        "commentCount": 320,
        "isAD": 0,
        "tags": ["热门"],
        "sttus": 1,
        "sortOrder": 0,
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-15T14:30:00"
      }
    ],
    "total": 50
  }
}
```

**字段说明：**

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | Long | 商品ID（bigint） |
| name | String | 商品名称 |
| image | String | 主图URL |
| images | String | 多图数组（JSON字符串，可为null） |
| price | Integer | 积分价格 |
| stock | Integer | 库存数量 |
| category | String | 商品分类编码 |
| brand | String | 品牌名称 |
| spec | String | 规格信息（文本或JSON字符串） |
| description | String | 商品描述 |
| sold | Integer | 销量 |
| commentCount | Integer | 评论数 |
| isAD | Integer | 是否推广（0-否 1-是） |
| tags | Array | 商品标签（从逗号分隔字段解析） |
| status | Integer | 状态（1-正常 2-下架 3-删除） |
| sortOrder | Integer | 排序权重 |
| createTime | String | 创建时间 |
| updateTime | String | 更新时间 |

---

### 2.2 获取商品详情

**接口地址：** `GET /points-mall/products/{productId}`

**功能描述：** 获取指定商品的详细信息

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| productId | String | 是 | 商品ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "1",
    "name": "10元车票优惠券",
    "image": "http://example.com/image1.jpg",
    "images": [
      "http://example.com/image1.jpg",
      "http://example.com/image2.jpg"
    ],
    "pointsPrice": 500,
    "originalPrice": 10,
    "tags": ["热门"],
    "description": "可用于购买任意车票抵扣10元",
    "specs": {
      "有效期": "30天",
      "使用范围": "全国通用",
      "使用限制": "每单限用一张"
    },
    "category": "coupons",
    "stock": 1000,
    "salesCount": 1250,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

---

### 2.3 兑换商品

**接口地址：** `POST /points-mall/exchange`

**功能描述：** 使用积分兑换商品

**请求参数：**

```json
{
  "productId": "1",
  "quantity": 2,
  "recipientName": "张三",
  "recipientPhone": "13800138000",
  "recipientAddress": "北京市朝阳区xxx路xxx号"
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| productId | String | 是 | 商品ID |
| quantity | Integer | 是 | 兑换数量 |
| recipientName | String | 是 | 收货人姓名 |
| recipientPhone | String | 是 | 联系电话 |
| recipientAddress | String | 是 | 收货地址 |

**响应示例：**

```json
{
  "code": 200,
  "message": "兑换成功",
  "data": {
    "success": true,
    "exchangeOrderNumber": "EX202401150001",
    "remainingPoints": 800
  }
}
```

**字段说明：**

| 字段 | 类型 | 说明 |
|-----|------|------|
| success | Boolean | 是否成功 |
| exchangeOrderNumber | String | 兑换订单号 |
| remainingPoints | Integer | 剩余积分 |

**错误响应：**

```json
{
  "code": 400,
  "message": "积分不足",
  "data": null
}
```

---

## 3. 搜索相关接口

### 3.1 获取搜索建议

**接口地址：** `GET /points-mall/search/suggest`

**功能描述：** 根据关键词获取搜索建议

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| keyword | String | 是 | 搜索关键词，至少2个字符 |
| limit | Integer | 否 | 返回数量限制，默认10 |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "suggestions": [
      {
        "type": "product",
        "id": "1",
        "name": "10元优惠券",
        "image": "http://example.com/image1.jpg",
        "pointsPrice": 500,
        "matchedText": "10元<em>优惠券</em>"
      },
      {
        "type": "category",
        "id": "coupons",
        "name": "优惠券",
        "matchedText": "<em>优惠券</em>"
      }
    ]
  }
}
```

**字段说明：**

| 字段 | 类型 | 说明 |
|-----|------|------|
| type | String | 建议类型：product(商品)、category(分类) |
| id | String | 商品ID或分类编码 |
| name | String | 名称 |
| image | String | 商品缩略图（仅商品类型） |
| pointsPrice | Integer | 积分价格（仅商品类型） |
| matchedText | String | 匹配的文本片段（高亮标记） |

---

### 3.2 获取热门搜索词

**接口地址：** `GET /points-mall/search/hot-keywords`

**功能描述：** 获取热门搜索关键词列表

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "keywords": [
      {
        "keyword": "旅行箱",
        "searchCount": 1520,
        "trend": "up"
      },
      {
        "keyword": "优惠券",
        "searchCount": 1200,
        "trend": "stable"
      }
    ]
  }
}
```

**字段说明：**

| 字段 | 类型 | 说明 |
|-----|------|------|
| keyword | String | 搜索关键词 |
| searchCount | Integer | 搜索次数 |
| trend | String | 趋势：up(上升)、down(下降)、stable(稳定) |

---

### 3.3 搜索埋点

**接口地址：** `POST /points-mall/search/track`

**功能描述：** 记录用户搜索行为，用于数据分析

**请求参数：**

```json
{
  "keyword": "旅行箱",
  "resultCount": 15,
  "userId": "user123"
}
```

**参数说明：**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| keyword | String | 是 | 搜索关键词 |
| resultCount | Integer | 是 | 结果数量 |
| userId | String | 否 | 用户ID（后端从Token获取） |

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true
  }
}
```

---

## 4. 数据库设计建议

### 4.1 商品表 (points_mall_product)

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | VARCHAR(32) | 主键 |
| name | VARCHAR(100) | 商品名称 |
| category | VARCHAR(20) | 分类 |
| points_price | INT | 积分价格 |
| original_price | DECIMAL(10,2) | 原价 |
| description | TEXT | 商品描述 |
| image | VARCHAR(200) | 主图URL |
| images | TEXT | 图片列表（JSON） |
| tags | VARCHAR(100) | 标签（JSON数组） |
| specs | TEXT | 规格（JSON对象） |
| stock | INT | 库存 |
| sales_count | INT | 销量 |
| status | TINYINT | 状态：0-下架，1-上架 |
| sort_order | INT | 排序 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 4.2 兑换订单表 (points_mall_exchange_order)

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | VARCHAR(32) | 主键 |
| order_number | VARCHAR(50) | 订单号 |
| user_id | VARCHAR(32) | 用户ID |
| product_id | VARCHAR(32) | 商品ID |
| product_name | VARCHAR(100) | 商品名称 |
| quantity | INT | 数量 |
| total_points | INT | 总积分 |
| recipient_name | VARCHAR(50) | 收货人 |
| recipient_phone | VARCHAR(20) | 联系电话 |
| recipient_address | VARCHAR(200) | 收货地址 |
| status | TINYINT | 状态：0-待发货，1-已发货，2-已完成，3-已取消 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 4.3 搜索记录表 (points_mall_search_log)

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键 |
| user_id | VARCHAR(32) | 用户ID |
| keyword | VARCHAR(100) | 搜索关键词 |
| result_count | INT | 结果数量 |
| create_time | DATETIME | 创建时间 |

---

## 5. 实现优先级

### 第一阶段（必须实现）

1. ✅ 获取用户积分信息 (`GET /points/info`)
2. ✅ 获取商品列表 (`GET /points-mall/products`)
3. ✅ 获取商品详情 (`GET /points-mall/products/{productId}`)
4. ✅ 兑换商品 (`POST /points-mall/exchange`)

### 第二阶段（搜索功能）

5. 获取搜索建议 (`GET /points-mall/search/suggest`)
6. 获取热门搜索词 (`GET /points-mall/search/hot-keywords`)
7. 搜索埋点 (`POST /points-mall/search/track`)

### 第三阶段（完善功能）

8. 获取积分明细 (`GET /points/history`)
9. 兑换订单查询
10. 兑换订单详情

---

## 6. 测试建议

### 6.1 单元测试

- 积分余额校验
- 库存扣减逻辑
- 并发兑换处理
- 搜索关键词匹配算法

### 6.2 接口测试

- 使用Postman或其他工具测试所有接口
- 验证参数校验逻辑
- 测试异常情况处理

### 6.3 性能测试

- 商品列表查询性能（建议使用索引）
- 搜索建议响应时间（< 200ms）
- 并发兑换压力测试

---

## 7. 安全建议

1. **认证授权**：所有接口都需要验证用户登录状态
2. **积分校验**：兑换前必须验证用户积分是否充足
3. **库存校验**：兑换前必须检查商品库存
4. **防重复提交**：使用订单号或Token防止重复兑换
5. **敏感信息加密**：用户地址、电话等信息建议加密存储
6. **日志记录**：记录所有兑换操作，便于审计

---

## 8. 前端调用示例

```javascript
// 获取商品列表
const products = await getProductList({
  page: 1,
  size: 12,
  category: 'coupons',
  sortBy: 'points-asc'
})

// 兑换商品
const result = await exchangeProduct({
  productId: '1',
  quantity: 2,
  recipientName: '张三',
  recipientPhone: '13800138000',
  recipientAddress: '北京市朝阳区xxx'
})
```

---

## 附录：前端已实现功能清单

✅ **第一阶段：核心功能（已完成）**
1. 积分商城页面基础布局
2. 商品网格展示
3. 分类筛选功能
4. 商品详情模态框
5. 数量选择和积分计算
6. 兑换确认模态框
7. 成功反馈模态框
8. API接口对接

🔄 **第二阶段：搜索功能（待实现）**
- 搜索输入框组件
- 搜索建议下拉
- 热门搜索词展示
- 搜索历史管理

📋 **第三阶段：交互优化（待实现）**
- 排序功能（已有UI，待后端支持）
- 视图切换（已有UI）
- 图片懒加载
- 无限滚动

---

**文档版本：** v1.0  
**最后更新：** 2024-12-01  
**维护人员：** 前端开发团队
