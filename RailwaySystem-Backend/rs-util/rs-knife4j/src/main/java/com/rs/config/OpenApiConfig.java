package com.rs.config;

import com.rs.model.Group;
import com.rs.properties.Knife4jProperties;
import com.rs.properties.OpenApiProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({OpenApiProperties.class, Knife4jProperties.class})
public class OpenApiConfig {

    private final OpenApiProperties openApiProperties;

    @Bean
    public List<GroupedOpenApi> openApi() {
        List<Group> groups = openApiProperties.getGroups();
        List<GroupedOpenApi> groupedOpenApis = new ArrayList<>();
        for (Group group : groups) {
            log.info("设置分组:{}, 扫描包:{}", group.getGroupName(), group.getMatchPath());
            groupedOpenApis.add(
                    GroupedOpenApi.builder()
                            .group(group.getGroupName())
                            .packagesToScan(group.getMatchPath().toArray(new String[0]))
                            .build()
            );
        }
        return groupedOpenApis;
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
