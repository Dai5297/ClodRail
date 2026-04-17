# 常见问题 FAQ

这份文档收集项目上手时最容易踩的坑,按**启动 / 配置 / 运行时 / 开发**四类整理。遇到问题优先按这份清单排查,再去提 Issue。

---

## 一、启动类

### Q1:`mvn clean install` 报 `package xxx does not exist`?

看下是否 `rs-api` 或 `rs-util/*` 没编译到本地仓库。完整编译需要在 `RailwaySystem-Backend` 根目录执行,否则子模块之间的依赖找不到。

```bash
cd RailwaySystem-Backend
mvn clean install -DskipTests
```

### Q2:启动服务时报 `Nacos connection refused`?

1. 确认 Nacos 已启动:`http://localhost:8848/nacos` 打得开
2. 确认服务的 `bootstrap.yaml` 里 `spring.cloud.nacos.config.server-addr` 填对
3. 确认 `namespace` 填的是 Nacos 里**实际存在**的命名空间 ID(不是名字,是 UUID)

### Q3:提示 `shared-mysql.yaml not found`?

本项目的所有共享配置都在 Nacos 里。第一次启动**必须**执行:

```bash
mysql -u root -p nacos < Docs/99-部署运维/nacos.sql
```

如果已经执行过仍然报错,检查:

- 导入的数据是否在当前 namespace 下(默认是 `public`)
- 服务的 `bootstrap.yaml` 里 `shared-configs` 列表是否完整

### Q4:Gateway 启动成功但请求返回 503?

常见原因:下游服务还没注册成功。Nacos 控制台 → "服务管理" → "服务列表",确认:

- `user-service`、`ticket-service` 等都在列表中
- 实例数 > 0,且状态为"健康"

### Q5:Seata Server 启动失败?

**先确认版本**:项目依赖的是 `org.apache.seata:seata-spring-boot-starter:2.3.0`,即 **Seata 2.x(Apache 捐赠后的新坐标)**。服务端也必须下载 **Seata 2.3.x**([Apache Seata 官网](https://seata.apache.org/download/seata-server/));Seata 1.x 包名是 `io.seata`,和项目代码不兼容。

如果用 `store.mode=db`,需要独立的 `seata` 库,并导入官方脚本([script/server/db/mysql.sql](https://github.com/apache/incubator-seata/blob/develop/script/server/db/mysql.sql)):

```sql
CREATE DATABASE seata DEFAULT CHARACTER SET utf8mb4;
-- 然后在 seata 库里执行官方 mysql.sql
```

并保证 `shared-seata.yaml` 的 `store.mode` 和 `server` 的实际模式一致。本地学习可以把 `store.mode` 改成 `file`,省去建表。

---

## 二、配置类

### Q6:AI 客服报 "未配置 API Key"?

在 Nacos `shared-langchain4j.yaml` 里配置阿里百炼的 API Key:

```yaml
langchain4j:
  qwen:
    api-key: sk-xxxxxxxxxxxxx
    model-name: qwen-plus
```

免费注册地址:https://bailian.console.aliyun.com/

### Q7:支付宝沙箱怎么配?

1. 登录 https://open.alipay.com/ → 沙箱环境
2. 拿到 `应用ID`、`应用私钥`、`支付宝公钥`
3. 填到 Nacos `shared-pay.yaml`
4. 沙箱账号买家可在沙箱控制台查看,不是真实账户

### Q8:Elasticsearch 没装会影响什么?

商城搜索会降级为 MySQL LIKE 查询,其它功能完全不受影响。可以先跳过 ES,等核心流程跑通再装。

---

## 三、运行时类

### Q9:下单提示"余票不足"但明明还有票?

优先级排查:

1. **Redis 余量 key 是否被错误初始化**——检查 `ticket:stock:{date}:{ticketId}:{seatType}`
2. **是否被别的并发下单扣光**——这是正确行为,不算 Bug
3. **热门标记是否错位**——`ticket:hot:{date}` 里是否包含这张票

手动重置一张票的 Redis 库存:

```bash
redis-cli SET ticket:stock:2026:05:01:1001:1 500
```

### Q10:TCC Rollback 没触发?

Seata TCC 依赖全局事务的 `@GlobalTransactional`:

```java
@GlobalTransactional(timeoutMills = 30000, name = "createOrder")
public OrderCreateResDTO createOrder(OrderCreateReqDTO reqDTO) {
    return orderService.createOrder(null, reqDTO);
}
```

常见问题:

- 没加 `@GlobalTransactional` → 不会有全局事务,TCC 也不触发
- 异常被 `try/catch` 吞掉了 → Seata 认为"Try 成功",会走 Commit 而不是 Rollback
- `exception` 配置没覆盖到实际抛的异常类

### Q11:WebSocket 连不上(客服页面)?

1. 确认 `rs-assistant` 的 18085 端口开着:`lsof -i:18085`
2. 浏览器 Console 看 WebSocket 握手请求,状态码一般是 101(Switching Protocols)
3. 前端 URL 要用 `ws://` 或 `wss://`,不要直接用 `http://`
4. 如果前端页面是 HTTPS,WS 也必须 WSS(浏览器 Mixed Content 限制)

### Q12:日志里疯狂刷 `ConnectionRefusedException`?

通常是某个中间件没启动。按优先级排查 Nacos → MySQL → Redis → RabbitMQ → Seata → ES。

### Q13:热更新了 Nacos 配置但服务没生效?

Spring Boot 对于 `@Value` 注入的字段需要 `@RefreshScope`,或者改用 `@ConfigurationProperties`。本项目大部分配置类都是 `@ConfigurationProperties`,已经自动热更新;少数 `@Value` 可能需要重启。

---

## 四、开发类

### Q14:新增一个业务服务,怎么快速起步?

1. 在 `rs-service` 下新建模块,`pom.xml` 继承 `rs-service-dependence`
2. 在 `bootstrap.yaml` 里声明 namespace + 共享配置
3. 在网关 `application.yaml` 里加一条路由
4. 在 `rs-api` 里定义对外接口和 Feign 客户端
5. 启动类标 `@EnableFeignClients(basePackages = "com.rs.client")`

### Q15:怎么加一个 Feign 调用?

在 `rs-api/client` 下写:

```java
@FeignClient(name = "ticket-service", contextId = "seatClient")
public interface SeatClient {
    @PostMapping("/inner/ticket/fetchSeat")
    List<Seat> fetchSeat(@RequestBody FetchSeatReqDTO reqDTO);
}
```

`contextId` 不能省略,否则同一个 `name` 下有多个接口会冲突。

### Q16:怎么在下游服务里拿到用户 ID?

网关已经把 `userId` 写入 `exchange.attributes.USER_INFO`,并通过 Header 传递。下游服务用 `UserContext.get()` 获取:

```java
Long userId = UserContext.get();
```

前提是引入 `rs-common` 的 `UserContextInterceptor`。

### Q17:代码风格检查?

暂无强制的 Checkstyle/Spotless 配置。建议遵循:

- 阿里巴巴 Java 开发手册
- 4 空格缩进
- 方法 50 行以内

后续可能接入 Spotless + Google Java Format,列在 ROADMAP。

---

## 五、还没答上的?

如果上面都解决不了:

1. 去 [GitHub Issues](https://github.com/Dai5297/ClodRail/issues) 搜一下是否有类似问题
2. 没有就提新 Issue,标题尽量具体。模板会引导你填环境、复现步骤、日志
3. 提 Issue 时带上:
   - 服务名 + 报错日志关键片段
   - 操作系统 / JDK 版本
   - 是否修改过默认配置
4. 也可以通过 [Discussions](https://github.com/Dai5297/ClodRail/discussions) 问更开放的问题

维护者会在工作日尽快回复。如果项目对你有帮助,也请点个 Star ⭐ 鼓励一下。
