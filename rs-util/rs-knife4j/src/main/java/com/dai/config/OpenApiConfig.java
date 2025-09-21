package com.dai.config;

import com.dai.properties.OpenApiProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    private final OpenApiProperties openApiProperties;

    @Bean
    public GroupedOpenApi openApi() {
        log.info("Knife4j配置, 扫描包: {}", openApiProperties.getMatchPath());

        return GroupedOpenApi.builder()
                .group(openApiProperties.getGroup())
                .packagesToScan(openApiProperties.getMatchPath().toArray(new String[0]))
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(openApiProperties.getTitle())
                        .description(openApiProperties.getDescription())
                        .version(openApiProperties.getVersion())
                        .contact(openApiProperties.getContact())
                        .license(openApiProperties.getLicense()));
    }
}
