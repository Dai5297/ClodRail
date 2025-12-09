package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "rs.es")
public class EsProperties {

    private List<String> nodes;
}
