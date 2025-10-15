package com.rs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 全局唯一Id工具类
 */
@Component
@RequiredArgsConstructor
public class RedisIdUtil {

    private final StringRedisTemplate stringRedisTemplate;

    private static final long BEGIN_TIMESTAMP = 1735689600L;

    public long nextId(String prefix) {
        // 1. 获取时间戳位
        LocalDateTime now = LocalDateTime.now();
        long epochSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = epochSecond - BEGIN_TIMESTAMP;
        // 2. 获取序列号位
        String data = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Long increment = stringRedisTemplate.opsForValue().increment("icr:" + prefix + ":" + data);
        return timestamp << 32 | increment;
    }

}
