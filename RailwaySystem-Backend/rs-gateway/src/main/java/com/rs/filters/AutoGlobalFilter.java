package com.rs.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.rs.constant.CommonConstant.ADMIN_PATH_PREFIX;
import static com.rs.constant.CommonConstant.USER_INFO;

@Component
public class AutoGlobalFilter implements GlobalFilter, Ordered {

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
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
        requestBuilder.headers(headers -> headers.remove(USER_INFO));
        if (userId != null && exchange.getRequest().getURI().getPath().startsWith(ADMIN_PATH_PREFIX)) {
            requestBuilder.header(USER_INFO, userId);
        }
        exchange = exchange.mutate().request(requestBuilder.build()).build();
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
