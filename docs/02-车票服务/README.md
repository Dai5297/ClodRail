# 车票服务 (rs-ticket)

车票服务负责车站、线路、车次、车票和座位管理。

## 服务信息

- **服务名**：ticket-service
- **端口**：18082
- **模块路径**：`RailwaySystem-Backend/rs-service/rs-ticket`

## 功能职责

- 车站管理（CRUD）
- 线路管理（途经站）
- 车次/列车管理
- 车票与余票查询
- 座位分配与库存

## API 路由

| 路由前缀 | 说明 |
|----------|------|
| `/customer/tickets/**` | 车票查询 |
| `/customer/stations/**` | 车站查询 |
| `/customer/seats/**` | 座位查询 |
| `/admin/tickets/**` | 车票管理 |
| `/admin/stations/**` | 车站管理 |
| `/admin/lines/**` | 线路管理 |
| `/admin/trains/**` | 车次管理 |
| `/inner/ticket/**` | 内部接口 |

## 文档列表

| 文档 | 说明 |
|------|------|
| [数据库设计.md](数据库设计.md) | 车票相关表结构 |

## 技术要点

- Redis 缓存热门车票
- XXL-JOB 定时预热库存
- 触发器自动生成座位
