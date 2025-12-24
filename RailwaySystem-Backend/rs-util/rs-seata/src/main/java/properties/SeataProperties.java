package properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rs.seata.nacos")
public class SeataProperties {

    /**
     * Nacos 服务器地址，对应配置项：rs.seata.nacos.serve-addr
     * 注意：字段名使用 serveAddr（因为配置是 serve-addr）
     */
    private String serveAddr = "localhost:8848";

    /**
     * Nacos 命名空间 ID
     */
    private String namespace = "";

    /**
     * Seata Server 在 Nacos 中注册的应用名
     */
    private String application = "seata-server";

    /**
     * Nacos 分组（可选，默认 DEFAULT_GROUP）
     */
    private String group = "DEFAULT_GROUP";
}
