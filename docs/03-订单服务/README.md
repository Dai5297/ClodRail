# 订单服务 (rs-order)

订单服务负责订单创建、支付、退票等功能。

## 服务信息

- **服务名**：order-service
- **端口**：18083
- **模块路径**：`RailwaySystem-Backend/rs-service/rs-order`

## 功能职责

- 订单创建与管理
- 支付集成（支付宝）
- 退票/改签处理
- 订单状态流转
- 订单超时处理

## API 路由

| 路由前缀 | 说明 |
|----------|------|
| `/customer/orders/**` | 用户订单 |
| `/customer/pay/**` | 支付接口 |
| `/admin/orders/**` | 订单管理 |
| `/common/orders/**` | 公共订单接口 |

## 文档列表

| 文档 | 说明 |
|------|------|
| [数据库设计.md](数据库设计.md) | 订单相关表结构 |

## 技术要点

- Seata 分布式事务（TCC/AT）
- RabbitMQ 异步处理
- XXL-JOB 订单超时任务
- Redis Lua 原子扣减
