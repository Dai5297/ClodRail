package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rs.ai")
public class DashscopeAiProperties {
    private String aliApiKey;
    private String chatModelName;
    private String streamModelName;
    private String embeddingModelName;
}
