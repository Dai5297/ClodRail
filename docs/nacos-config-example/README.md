# Nacos 配置示例（可提交版本）

本目录用于提供 **可提交到仓库** 的 Nacos 配置示例。

- 这些文件的所有密码/Key 都使用占位符（例如 `<MYSQL_PASSWORD>`）。
- 你需要在本地/Nacos 中将占位符替换为真实值。

## 建议导入方式

1. 启动 Nacos（默认：`http://localhost:8848/nacos`，账号/密码：nacos/nacos）
2. 创建命名空间（建议名：`railway-system`），并记录 namespaceId
3. 将本目录下的 `shared-*.yaml` 作为配置导入 Nacos（DataId 与文件名保持一致）
4. 各微服务的 `bootstrap.yaml` 已通过 `spring.config.import` 引用这些 DataId

## 文件清单

- `shared-mysql.yaml`
- `shared-redis.yaml`
- `shared-rabbitmq.yaml`
- `shared-seata.yaml`
- `shared-alipay.yaml`
- `shared-langchain4j.yaml`
- `shared-knife4j.yaml`
