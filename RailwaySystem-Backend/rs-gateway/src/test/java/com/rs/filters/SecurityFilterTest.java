package com.rs.filters;

import com.rs.properties.ExcludeProperties;
import com.rs.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.rs.constant.CommonConstant.AUTH_PREFIX;
import static com.rs.constant.CommonConstant.USER_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityFilterTest {

    private ExcludeProperties excludeProperties;
    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        JWTUtil.initialize("test-jwt-key-1234567890-abcdef123456");
        excludeProperties = new ExcludeProperties();
        excludeProperties.setPaths(List.of("/customer/auth/**", "/admin/auth/**"));
        securityFilter = new SecurityFilter(null, excludeProperties);
    }

    @Test
    void customerRequestShouldAcceptAccessToken() {
        String accessToken = JWTUtil.createAccessToken(123L, 60_000L);
        MockServerHttpRequest request = MockServerHttpRequest.get("/customer/user/info")
                .header(HttpHeaders.AUTHORIZATION, AUTH_PREFIX + accessToken)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = currentExchange -> {
            assertEquals(Long.valueOf(123L), currentExchange.getAttribute(USER_INFO));
            return Mono.empty();
        };

        securityFilter.filter(exchange, chain).block();
        assertNotNull(exchange.getAttribute(USER_INFO));
    }

    @Test
    void customerRequestShouldRejectMalformedToken() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/customer/user/info")
                .header(HttpHeaders.AUTHORIZATION, AUTH_PREFIX + "invalid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = currentExchange -> Mono.error(new IllegalStateException("should not enter chain"));

        securityFilter.filter(exchange, chain).block();
        assertNull(exchange.getAttribute(USER_INFO));
    }
}
