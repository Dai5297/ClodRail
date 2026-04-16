package com.rs.config;

import com.rs.handler.GlobalExceptionHandler;
import com.rs.properties.AuthProperties;
import com.rs.util.JWTUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AopAutoConfiguration {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public InitializingBean jwtUtilInitializer(AuthProperties authProperties) {
        return () -> JWTUtil.initialize(authProperties.getJwtKey());
    }
}
