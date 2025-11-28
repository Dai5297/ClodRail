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
        if (authorization == null) {
            return chain.filter(exchange);
        }
        String uuid = authorization.get(0).substring(AUTH_PREFIX.length());
        String token;
        String key = null;
        URI uri = exchange.getRequest().getURI();
        if (uri.getPath().startsWith(USER_PATH_PREFIX)) {
            key = USER_LOGIN_TOKEN + uuid;
        } else if (uri.getPath().startsWith(ADMIN_PATH_PREFIX)) {
            key = ADMIN_LOGIN_TOKEN + uuid;
        } else {
            return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
        }
        token = stringRedisTemplate.opsForValue().get(key);
        // 解析token并获取用户信息
        Claims claims;
        String subject;
        try {
            claims = JWTUtil.parseJWT(token);
            subject = claims.getSubject();
        } catch (Exception e) {
            return handleError(exchange, RespCode.UNAUTHORIZED, "认证失败");
        }
        // 续期redisKey
        Long expire = stringRedisTemplate.getExpire(USER_LOGIN_TOKEN + uuid, TimeUnit.SECONDS);

        if (expire > 0 && expire < RedisUserKeyConstant.USER_TOKEN_LEAST_TTL) {
            // 使用秒为单位，与getExpire保持一致
            stringRedisTemplate.expire(USER_LOGIN_TOKEN + uuid,
                    RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.SECONDS);
            log.debug("用户token续期成功，uuid: {}", uuid);
        }
        // 根据请求路径将claim转为对应实体类
        if (exchange.getRequest().getPath().toString().startsWith(ADMIN_PATH_PREFIX)) {
            // 将Subject转为Admin对应实体类
            Admin admin = JSONUtil.toBean(subject, Admin.class);
            exchange.getAttributes().put(USER_INFO, admin.getId());
            return checkPermission(admin, exchange, chain);
        } else if (exchange.getRequest().getPath().toString().startsWith(USER_PATH_PREFIX)) {
            // 将Subject转为User对应实体类
            User bean = JSONUtil.toBean(subject, User.class);
            exchange.getAttributes().put(USER_INFO, bean.getId());
            return chain.filter(exchange);
        } else {
            return handleError(exchange, RespCode.NOT_FOUND, "未知请求路径");
        }
    }

    private Mono<Void> checkPermission(Admin admin, ServerWebExchange exchange, WebFilterChain chain) {
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
