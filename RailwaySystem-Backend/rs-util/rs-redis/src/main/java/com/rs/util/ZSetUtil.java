package com.rs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ZSetUtil {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取最近访问最少的节点
     *
     * @param key 键
     * @return 节点
     */
    public Object leastCountNode(String key) {
        String script = """
                    local min_members = redis.call('ZRANGE', KEYS[1], 0, 0, 'WITHSCORES')
                    if #min_members == 0 then return nil end
                    local member = min_members[1]
                    redis.call('ZINCRBY', KEYS[1], 1, member)
                    return member
                """;

        RedisScript<String> redisScript = RedisScript.of(script, String.class);
        List<String> keys = Collections.singletonList(key);
        return stringRedisTemplate.execute(redisScript, keys);
    }
}
