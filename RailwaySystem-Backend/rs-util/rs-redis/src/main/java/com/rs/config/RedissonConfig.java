package com.rs.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    /**
     * redis单例实例连接前缀
     */
    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    /**
     * redis集群实例连接前缀
     */
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

//    private final RSRedisProperties properties;

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        log.info("开始创建Redisson客户端");
//        BeanUtil.copyProperties(properties, redisProperties);
        // Redis集群配置
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        // Redis哨兵配置
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        Config config = new Config();
        int timeout = 3000;
        Duration d = redisProperties.getTimeout();
        if (d != null) {
            timeout = Long.valueOf(d.toMillis()).intValue();
        }
        if (cluster != null && !CollectionUtil.isEmpty(cluster.getNodes())) {
            // 集群模式
            config.useClusterServers()
                    .addNodeAddress(cluster.getNodes().toArray(new String[0]))
                    .setTimeout(timeout)
                    .setPassword(redisProperties.getPassword());
        } else if (sentinel != null && !StrUtil.isEmpty(sentinel.getMaster())) {
            // 哨兵模式
            config.useSentinelServers()
                    .setMasterName(sentinel.getMaster())
                    .addSentinelAddress(convert(sentinel.getNodes()))
                    .setConnectTimeout(timeout)
                    .setDatabase(redisProperties.getDatabase())
                    .setPassword(redisProperties.getPassword());
        } else {
            // 单机模式
            config.useSingleServer()
                    .setAddress(String.format("redis://%s:%d", redisProperties.getHost(), redisProperties.getPort()))
                    .setConnectTimeout(timeout)
                    .setDatabase(redisProperties.getDatabase())
                    .setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }

    private String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
                nodes.add(REDIS_PROTOCOL_PREFIX + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[0]);
    }
}
