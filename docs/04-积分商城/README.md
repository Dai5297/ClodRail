# 积分商城服务 (rs-mall)

积分商城服务负责积分管理、商品展示与兑换。

## 服务信息

- **服务名**：mall-service
- **端口**：18086
- **模块路径**：`RailwaySystem-Backend/rs-service/rs-mall`

## 功能职责

- 用户积分管理
- 商品展示与搜索（ES）
- 积分兑换下单
- 管理端商品管理

## API 路由

| 路由前缀 | 说明 |
|----------|------|
| `/customer/points/**` | 积分接口 |
| `/customer/mall/**` | 商城接口 |
| `/admin/mall/**` | 商城管理 |

## 文档列表

| 文档 | 说明 |
|------|------|
| [数据库设计.md](数据库设计.md) | 商城相关表结构 |

## 技术要点

- Elasticsearch 商品搜索
- Canal + RabbitMQ 索引同步
- 积分事务处理
