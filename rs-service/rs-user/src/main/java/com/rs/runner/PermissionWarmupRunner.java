package com.rs.runner;

import com.rs.mapper.PermissionMapper;
import com.rs.model.customer.AdminPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.rs.constant.RedisUserKeyConstant.AUTH_ROLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionWarmupRunner implements ApplicationRunner {

    private final PermissionMapper permissionMapper;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<AdminPermission> apis = permissionMapper.queryAllApis();
        if (apis == null || apis.isEmpty()) {
            log.warn("No admin api permissions found in DB (admin_permission.type=2). Skip warmup.");
            return;
        }

        Map<Integer, Set<String>> roleToApis = new HashMap<>();
        for (AdminPermission p : apis) {
            if (p == null || p.getRole() == null || p.getPermission() == null || p.getPermission().isBlank()) {
                continue;
            }
            roleToApis.computeIfAbsent(p.getRole(), k -> new HashSet<>()).add(p.getPermission());
        }

        int total = 0;
        for (Map.Entry<Integer, Set<String>> entry : roleToApis.entrySet()) {
            Integer role = entry.getKey();
            Set<String> perms = entry.getValue();
            String redisKey = AUTH_ROLE + role + ":apis";
            // 覆盖式刷新：以DB为准
            stringRedisTemplate.delete(redisKey);
            if (!perms.isEmpty()) {
                stringRedisTemplate.opsForSet().add(redisKey, perms.toArray(new String[0]));
                total += perms.size();
            }
        }

        log.info("Admin permission warmup done. roles={}, totalApis={}.", roleToApis.size(), total);
    }
}
