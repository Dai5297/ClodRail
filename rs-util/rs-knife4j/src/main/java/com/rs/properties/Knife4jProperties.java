package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rs.knife4j")
public class Knife4jProperties {
    private String title = "API服务";
    private String description = "API服务";
    private String version = "v1.0";
    private Contact contact = new Contact();

    @Data
    public static class Contact {
        private String name = "dai";
        private String email = "15160255297@163.com";
        private String url = "https://github.com/Dai5297";
    }
}
