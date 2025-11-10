package com.rs.config;

import com.rs.aspect.LockAspect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class LockConfig {

    private final RedissonClient redissonClient;

    @Bean
    public LockAspect lockAspect() {
        log.info("初始化分布式锁切面...");
        return new LockAspect(redissonClient);
    }
}
