package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rs.ali-oss")
public class AliOssProperties {

    private Boolean enable = false;

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;
}
