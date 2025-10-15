package com.rs.filters;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.rs.constant.CommonConstant;
import com.rs.constant.RedisKeyConstant;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.model.customer.User;
import com.rs.properties.ExcludeProperties;
import com.rs.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoGlobalFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate stringRedisTemplate;

    private final ExcludeProperties excludeProperties;

    /**
     * 拦截器
     * @param exchange 网关上下文信息
     * @param chain 过滤器链
     * @return 响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //  获取请求
        ServerHttpRequest request = exchange.getRequest();
        // 放行无关请求
        if (isExclude(request.getPath().value())) {
            return chain.filter(exchange);
        }
        // 获取token
        HttpHeaders headers = request.getHeaders();
        List<String> authorization = headers.get("Authorization");
        String token = null;
        String uuid = null;
        if (authorization != null) {
            uuid = authorization.get(0).substring(7);
        }
        String key = RedisKeyConstant.USER_LOGIN_TOKEN + uuid;
        token = stringRedisTemplate.opsForValue().get(key);
        // 解析token并获取用户信息
        Claims claims = null;
        try {
            claims = JWTUtil.parseJWT(token);
        } catch (Exception e) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }
        String userStr = claims.getSubject();
        if (ObjectUtil.isEmpty(userStr)) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }
        User user = JSONUtil.toBean(userStr, User.class);
        // 续期redisKey
        Long expire = stringRedisTemplate.getExpire(RedisKeyConstant.USER_LOGIN_TOKEN + uuid, TimeUnit.SECONDS);

        if (expire != null && expire > 0 && expire < RedisKeyConstant.USER_TOKEN_LEAST_TTL) {
            // 使用秒为单位，与getExpire保持一致
            stringRedisTemplate.expire(RedisKeyConstant.USER_LOGIN_TOKEN + uuid,
                    RedisKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.SECONDS);
            log.debug("用户token续期成功，uuid: {}", uuid);
        }
        // 添加用户信息
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header(CommonConstant.USER_INFO, String.valueOf(user.getId())))
                .build();
        return chain.filter(swe);
    }

    /**
     * 优先级
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 放行路径
     * @param path 请求路径
     * @return 是否放行
     */
    private boolean isExclude(String path) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String excludePath : excludeProperties.getPaths()) {
            if (antPathMatcher.match(excludePath, path) || excludePath.equals(path)) {
                return true;
            }
        }
        return false;
    }
}
