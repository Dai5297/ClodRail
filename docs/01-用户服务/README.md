# 用户服务 (rs-user)

用户服务负责用户认证、权限管理等功能。

## 服务信息

- **服务名**：user-service
- **端口**：18081
- **模块路径**：`RailwaySystem-Backend/rs-service/rs-user`

## 功能职责

- 用户注册/登录/认证
- 乘车人/联系人信息维护
- 管理员登录与用户管理
- RBAC 权限控制

## API 路由

| 路由前缀 | 说明 |
|----------|------|
| `/customer/user/**` | 用户端用户接口 |
| `/customer/contact/**` | 联系人管理 |
| `/customer/auth/**` | 用户认证 |
| `/admin/auth/**` | 管理员认证 |
| `/admin/user/**` | 用户管理 |
| `/admin/admins/**` | 管理员管理 |

## 文档列表

| 文档 | 说明 |
|------|------|
| [数据库设计.md](数据库设计.md) | 用户相关表结构 |

## 技术要点

- JWT + Redis 会话管理
- Spring Security RBAC
- 密码加密存储
