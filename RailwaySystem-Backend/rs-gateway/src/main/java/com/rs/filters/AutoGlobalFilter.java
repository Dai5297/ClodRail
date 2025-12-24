package com.rs.filters;

import com.rs.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.rs.constant.CommonConstant.AUTHENTICATION;
import static com.rs.constant.CommonConstant.USER_INFO;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoGlobalFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate stringRedisTemplate;


    /**
     * 拦截器
     *
     * @param exchange 网关上下文信息
     * @param chain    过滤器链
     * @return 响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Object userInfo = exchange.getAttributes().get(USER_INFO);
        String userId = (userInfo != null) ? String.valueOf(userInfo) : null;
        if (userId != null) {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(USER_INFO, userId)
                    .header(AUTHENTICATION, String.valueOf(exchange.getRequest().getHeaders().get(CommonConstant.AUTHENTICATION)))
                    .build();
            exchange = exchange.mutate().request(request).build();
        }

        return chain.filter(exchange);
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
}
