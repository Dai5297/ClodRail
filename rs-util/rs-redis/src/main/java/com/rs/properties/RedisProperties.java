package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rs.redis")
public class RedisProperties {

    private String host = "localhost";
    private int port = 6379;
    private String password = "";
    private int timeout = 2000;
    private int database = 0;

    private Pool pool = new Pool();

    @Data
    public static class Pool {
        private int maxActive = 8;
        private int maxIdle = 8;
        private int minIdle = 0;
        private int maxWait = -1; // -1 表示无限等待
    }
}
