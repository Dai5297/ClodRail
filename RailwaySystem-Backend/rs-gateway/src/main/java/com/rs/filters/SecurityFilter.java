package com.rs.filters;

import cn.hutool.json.JSONUtil;
import com.rs.constant.RedisUserKeyConstant;
import com.rs.enums.RespCode;
import com.rs.model.RespResult;
import com.rs.model.customer.Admin;
import com.rs.model.customer.User;
import com.rs.properties.ExcludeProperties;
import com.rs.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.rs.constant.CommonConstant.*;
import static com.rs.constant.RedisUserKeyConstant.*;

@Slf4j
@Component
@Order(-100)
@RequiredArgsConstructor
public class SecurityFilter implements WebFilter {

    private final StringRedisTemplate stringRedisTemplate;

    // 排除路径
    private final ExcludeProperties excludeProperties;

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        if (isExclude(exchange.getRequest().getPath().toString())) {
            return chain.filter(exchange);
        }
        // 解析JWT获取信息
        List<String> authorization = exchange.getRequest().getHeaders().get(AUTHENTICATION);
        if (authorization == null || authorization.isEmpty()) {
            return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
        }
        String authorizationValue = authorization.get(0);
        if (!authorizationValue.startsWith(AUTH_PREFIX) || authorizationValue.length() <= AUTH_PREFIX.length()) {
            return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
        }
        String rawToken = authorizationValue.substring(AUTH_PREFIX.length());
        URI uri = exchange.getRequest().getURI();
        if (uri.getPath().startsWith(USER_PATH_PREFIX)) {
            return authenticateCustomer(rawToken, exchange, chain);
        }
        if (!uri.getPath().startsWith(ADMIN_PATH_PREFIX)) {
            return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
        }
        return authenticateAdmin(rawToken, exchange, chain);
    }

    private Mono<Void> authenticateCustomer(String accessToken, ServerWebExchange exchange, WebFilterChain chain) {
        try {
            Claims claims = JWTUtil.parseJWT(accessToken);
            if (!JWTUtil.isAccessToken(claims)) {
                return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
            }
            Long userId = JWTUtil.getUserId(claims);
            if (userId == null) {
                return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
            }
            exchange.getAttributes().put(USER_INFO, userId);
            return chain.filter(exchange);
        } catch (Exception e) {
            return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
        }
    }

    private Mono<Void> authenticateAdmin(String uuid, ServerWebExchange exchange, WebFilterChain chain) {
        String key = ADMIN_LOGIN_TOKEN + uuid;
        String token;
        token = stringRedisTemplate.opsForValue().get(key);
        if (token == null || token.isEmpty()) {
            return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
        }

        Claims claims;
        String subject;
        try {
            claims = JWTUtil.parseJWT(token);
            subject = claims.getSubject();
        } catch (Exception e) {
            return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
        }
        // 续期redisKey
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);

        if (expire != null && expire > 0 && expire < RedisUserKeyConstant.USER_TOKEN_LEAST_TTL) {
            stringRedisTemplate.expire(key,
                    RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.MILLISECONDS);
            log.debug("用户token续期成功，uuid: {}", uuid);
        }
        Admin admin = JSONUtil.toBean(subject, Admin.class);
        exchange.getAttributes().put(USER_INFO, admin.getId());
        return checkPermission(admin, exchange, chain);
    }

    private Mono<Void> checkPermission(Admin admin, ServerWebExchange exchange, WebFilterChain chain) {
        if (Objects.equals(admin.getRole(), 104)) {
            return chain.filter(exchange);
        }
        String key = AUTH_ROLE + admin.getRole() + ":apis";
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        if (members != null) {
            for (String member : members) {
                String[] split = member.split("-");
                String requestMethod = split[0];
                String requestPath = split[1];
                requestPath = "/" + requestPath.replace(":", "/");
                if (Objects.equals(requestMethod, exchange.getRequest().getMethod().toString())
                        && ANT_PATH_MATCHER.match(requestPath, exchange.getRequest().getURI().getPath())) {
                    return chain.filter(exchange);
                }
            }
        }
        return handleError(exchange, RespCode.FORBIDDEN, "无访问权限");
    }

    /**
     * 放行路径
     *
     * @param path 请求路径
     * @return 是否放行
     */
    private boolean isExclude(String path) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if (excludeProperties.getPaths() == null) {
            return false;
        }
        for (String excludePath : excludeProperties.getPaths()) {
            if (antPathMatcher.match(excludePath, path) || excludePath.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> handleError(ServerWebExchange exchange, RespCode code, String message) {
        RespResult<Object> error = RespResult.error(code, message);
        return exchange.getResponse()
                .writeWith(
                        Mono.just(
                                exchange.getResponse()
                                        .bufferFactory()
                                        .wrap(JSONUtil.toJsonStr(error).getBytes())));
    }
}
