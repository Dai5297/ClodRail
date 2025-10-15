package config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import properties.SeataProperties;

@Configuration
@EnableConfigurationProperties(SeataProperties.class)
public class SeataConfig {
}
