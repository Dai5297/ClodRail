package properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rs.seata")
public class SeataProperties {

    private Registry registry = new Registry();

    @Data
    public static class Registry {
        private String type = "nacos";
        private Nacos nacos = new Nacos();

        @Data
        public static class Nacos {
            /**
             * 注意：字段名应为 serverAddr（对应 server-addr）
             * 但你原始配置中写的是 serve-addr（拼写错误）
             * 此处按正确语义使用 serverAddr，若需保留错误拼写，请改为 serveAddr
             */
            private String serverAddr = "localhost:8848";
            private String namespace = "";
            private String group = "DEFAULT_GROUP";
            private String application = "seata-server";

        }
    }
}
