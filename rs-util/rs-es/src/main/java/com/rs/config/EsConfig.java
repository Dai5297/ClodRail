package com.rs.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.rs.core.ElasticSearchTemplate;
import com.rs.core.impl.ElasticSearchTemplateImpl;
import com.rs.properties.EsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(EsProperties.class)
public class EsConfig {

    private final EsProperties esProperties;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        MAPPER.registerModule(javaTimeModule);

    }

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        List<String> nodes = esProperties.getNodes();
        HttpHost[] httpHosts = new HttpHost[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            String node = nodes.get(i); // 例如 "localhost:9200"

            // 拆分主机和端口
            String[] parts = node.split(":");
            String hostname = parts[0];
            int port = Integer.parseInt(parts[1]);

            // 使用正确的 HttpHost 构造函数
            httpHosts[i] = new HttpHost(hostname, port, "http"); // 默认使用 http 协议
        }
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    @Bean
    public ElasticSearchTemplate template(RestHighLevelClient restHighLevelClient) {
        return new ElasticSearchTemplateImpl(restHighLevelClient);
    }
}
