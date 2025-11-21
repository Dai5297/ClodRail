package com.rs.filters;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.rs.constant.CommonConstant;
import com.rs.constant.RedisUserKeyConstant;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.model.RespResult;
import com.rs.model.customer.User;
import com.rs.properties.ExcludeProperties;
import com.rs.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.rs.constant.CommonConstant.ADMIN_PATH_PREFIX;
import static com.rs.constant.CommonConstant.USER_PATH_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoGlobalFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate stringRedisTemplate;

    private final ExcludeProperties excludeProperties;

    /**
     * 拦截器
     *
     * @param exchange 网关上下文信息
     * @param chain    过滤器链
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

        String path = request.getURI().getPath();
        if (path.startsWith(USER_PATH_PREFIX)) {
            return userAuth(exchange, chain, request);
        }else if (path.startsWith(ADMIN_PATH_PREFIX)) {
            return adminAuth(exchange, chain, request);
        }
        return blockResponse(exchange);
    }

    private Mono<Void> adminAuth(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request) {
        return null;
    }

    private Mono<Void> userAuth(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request) {

        User user = (User) praseJwtAndRenewalJwt(request, User.class);
        // 添加用户信息
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header(CommonConstant.USER_INFO, String.valueOf(user.getId())))
                .build();
        return chain.filter(swe);
    }

    private Object praseJwtAndRenewalJwt(ServerHttpRequest request, Class<?> classType) {
        // 获取token
        HttpHeaders headers = request.getHeaders();
        List<String> authorization = headers.get("Authorization");
        String uuid = null;
        if (authorization != null) {
            uuid = authorization.get(0).substring(7);
        }
        String token = null;
        String key = RedisUserKeyConstant.USER_LOGIN_TOKEN + uuid;
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
        // 续期redisKey
        Long expire = stringRedisTemplate.getExpire(RedisUserKeyConstant.USER_LOGIN_TOKEN + uuid, TimeUnit.SECONDS);

        if (expire != null && expire > 0 && expire < RedisUserKeyConstant.USER_TOKEN_LEAST_TTL) {
            // 使用秒为单位，与getExpire保持一致
            stringRedisTemplate.expire(RedisUserKeyConstant.USER_LOGIN_TOKEN + uuid,
                    RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.SECONDS);
            log.debug("用户token续期成功，uuid: {}", uuid);
        }
        return JSONUtil.toBean(userStr, classType);
    }

    /**
     * 优先级
     *
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return 0;
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


    /**
     * 拦截请求并返回自定义 JSON 响应
     */
    private Mono<Void> blockResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        // 构造响应体（例如：{"code":403,"msg":"无权限访问"}）
        RespResult errorResult = RespResult.error(RespCode.FORBIDDEN);

        // 将字符串转为 DataBuffer（响应体必须是 DataBuffer）
        DataBuffer buffer = response.bufferFactory()
                .wrap(JSONUtil.toJsonStr(errorResult).getBytes(StandardCharsets.UTF_8));

        // 写入响应并结束
        return response.writeWith(Mono.just(buffer));
    }
}
