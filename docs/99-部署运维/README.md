# 部署运维

本目录包含项目部署和运维相关文档。

## 文档列表

| 文档 | 说明 |
|------|------|
| [环境要求.md](环境要求.md) | 运行环境要求 |
| [快速启动.md](快速启动.md) | 本地启动指南 |
| [nacos.sql](nacos.sql) | Nacos 数据库初始化脚本 |

## Nacos 共享配置说明

`nacos.sql` 包含完整的 Nacos 配置，导入后自动生成以下共享配置：

| 配置文件 | 说明 |
|----------|------|
| shared-mysql.yaml | MySQL 数据源、HikariCP 连接池、MyBatis |
| shared-redis.yaml | Redis 连接、Lettuce 连接池 |
| shared-rabbitmq.yaml | RabbitMQ 连接、生产者/消费者配置 |
| shared-seata.yaml | Seata 分布式事务注册中心配置 |
| shared-knife4j.yaml | Knife4j API 文档配置 |
| shared-alipay.yaml | 支付宝沙箱支付配置 |
| shared-langchain4j.yaml | 阿里通义大模型配置 |
| seata-server.properties | Seata Server 配置 |

## 服务端口速查

| 服务 | 端口 |
|------|------|
| Nacos | 8848 |
| MySQL | 3306 |
| Redis | 6379 |
| RabbitMQ | 5672 / 15672 |
| Seata | 8091 |
| rs-gateway | 18080 |
| rs-user | 18081 |
| rs-ticket | 18082 |
| rs-order | 18083 |
| rs-assistant | 18084 |
| Netty WS | 18085 |
| rs-mall | 18086 |
