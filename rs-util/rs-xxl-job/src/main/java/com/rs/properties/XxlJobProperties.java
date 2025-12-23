package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rs.xxl-job")
public class XxlJobProperties {

    private String adminAddresses;

    private String appName;

    private String ip;

    private Integer port;

    private String accessToken;
}
