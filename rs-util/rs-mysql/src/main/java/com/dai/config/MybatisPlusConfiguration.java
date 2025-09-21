package com.dai.config;

import com.dai.interceptor.MybatisAutoFillInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MybatisPlusConfiguration {

    @Bean
    public MybatisAutoFillInterceptor mybatisAutoFillInterceptor() {
        log.info("创建 MybatisAutoFillInterceptor");
        return new MybatisAutoFillInterceptor();
    }

    /**
     * 使用正确的方法 - 直接设置插件数组
     */
    @Bean
    public SqlSessionFactoryBeanCustomizer sqlSessionFactoryBeanCustomizer(MybatisAutoFillInterceptor autoFillInterceptor) {
        return (SqlSessionFactoryBean factoryBean) -> {
            log.info("配置 SqlSessionFactoryBean 自定义器");

            // 直接设置插件数组
            factoryBean.setPlugins(autoFillInterceptor);
            log.info("MybatisAutoFillInterceptor 注册成功");
        };
    }
}