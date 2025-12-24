package com.rs.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.rs.properties.XxlJobProperties;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobConfiguration {

    private final XxlJobProperties xxlJobProperties;

    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor() {
        log.info("============================== xxlJobSpringExecutor ==============================");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(xxlJobProperties.getAppName());
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobSpringExecutor.setIp(xxlJobProperties.getIp());
        xxlJobSpringExecutor.setPort(xxlJobProperties.getPort());
        xxlJobSpringExecutor.setLogPath("/Users/dai/Documents/GitHub/xxl-job/logs/xxl-job");
        log.info("============================== xxlJobSpringExecutor ==============================");

        return xxlJobSpringExecutor;
    }

}
