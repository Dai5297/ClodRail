package com.rs.handler;

import cn.hutool.json.JSONUtil;
import com.rs.client.user.UserClient;
import com.rs.constant.RedisUserKeyConstant;
import com.rs.model.customer.Admin;
import com.rs.model.customer.User;
import com.rs.util.JWTUtil;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;

import static com.rs.constant.AttributeKeyConstant.AGENT;
import static com.rs.constant.AttributeKeyConstant.SESSION_ID;
import static com.rs.constant.AttributeKeyConstant.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenHandlerTest {

    private static final String TEST_JWT_KEY = "test-jwt-key-1234567890-abcdef123456";

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        JWTUtil.initialize(TEST_JWT_KEY);
    }

    @Test
    void shouldAuthenticateUserWebsocketWithAccessToken() {
        TokenHandler handler = new TokenHandler(stringRedisTemplate, userClient);
        EmbeddedChannel channel = new EmbeddedChannel(handler, new ChannelInboundHandlerAdapter());
        String accessToken = JWTUtil.createAccessToken(1001L, 60_000L);
        when(userClient.usernameList(List.of(1001L))).thenReturn(Map.of(1001L, "alice"));

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/ws/assistant/user?sessionId=1001_123&token=" + accessToken
        );

        assertTrue(channel.writeInbound(request));
        User user = channel.attr(USER).get();

        assertNotNull(user);
        assertEquals(1001L, user.getId());
        assertEquals("alice", user.getUsername());
        assertEquals("1001_123", channel.attr(SESSION_ID).get());
        verify(stringRedisTemplate, never()).getExpire(startsWith(RedisUserKeyConstant.USER_LOGIN_TOKEN), any());
    }

    @Test
    void shouldFallbackToLegacyUserRedisToken() {
        TokenHandler handler = new TokenHandler(stringRedisTemplate, userClient);
        EmbeddedChannel channel = new EmbeddedChannel(handler, new ChannelInboundHandlerAdapter());
        User legacyUser = new User();
        legacyUser.setId(1002L);
        legacyUser.setUsername("legacy");
        String legacyJwt = JWTUtil.createJWT(JSONUtil.toJsonStr(legacyUser));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisUserKeyConstant.USER_LOGIN_TOKEN + "legacy-token")).thenReturn(legacyJwt);
        when(stringRedisTemplate.getExpire(RedisUserKeyConstant.USER_LOGIN_TOKEN + "legacy-token", java.util.concurrent.TimeUnit.MILLISECONDS))
                .thenReturn(RedisUserKeyConstant.USER_TOKEN_LEAST_TTL - 1);

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/ws/assistant/user?sessionId=1002_123&token=legacy-token"
        );

        assertTrue(channel.writeInbound(request));
        User user = channel.attr(USER).get();

        assertNotNull(user);
        assertEquals(1002L, user.getId());
        assertEquals("legacy", user.getUsername());
        verify(stringRedisTemplate).expire(
                RedisUserKeyConstant.USER_LOGIN_TOKEN + "legacy-token",
                RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL,
                java.util.concurrent.TimeUnit.MILLISECONDS
        );
    }

    @Test
    void shouldAuthenticateAdminWithRedisTokenAndRenewAdminKey() {
        TokenHandler handler = new TokenHandler(stringRedisTemplate, userClient);
        EmbeddedChannel channel = new EmbeddedChannel(handler, new ChannelInboundHandlerAdapter());
        Admin admin = new Admin();
        admin.setId(2001L);
        admin.setUsername("admin");
        admin.setRole(104);
        String adminJwt = JWTUtil.createJWT(JSONUtil.toJsonStr(admin));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(RedisUserKeyConstant.ADMIN_LOGIN_TOKEN + "admin-token")).thenReturn(adminJwt);
        when(stringRedisTemplate.getExpire(RedisUserKeyConstant.ADMIN_LOGIN_TOKEN + "admin-token", java.util.concurrent.TimeUnit.MILLISECONDS))
                .thenReturn(RedisUserKeyConstant.USER_TOKEN_LEAST_TTL - 1);

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/ws/assistant/agent?sessionId=session-1&token=admin-token"
        );

        assertTrue(channel.writeInbound(request));
        Admin channelAdmin = channel.attr(AGENT).get();

        assertNotNull(channelAdmin);
        assertEquals(2001L, channelAdmin.getId());
        assertEquals("session-1", channel.attr(SESSION_ID).get());
        verify(stringRedisTemplate).expire(
                RedisUserKeyConstant.ADMIN_LOGIN_TOKEN + "admin-token",
                RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL,
                java.util.concurrent.TimeUnit.MILLISECONDS
        );
    }

    @Test
    void shouldRejectUserWebsocketWhenSessionDoesNotBelongToUser() {
        TokenHandler handler = new TokenHandler(stringRedisTemplate, userClient);
        EmbeddedChannel channel = new EmbeddedChannel(handler, new ChannelInboundHandlerAdapter());
        String accessToken = JWTUtil.createAccessToken(1001L, 60_000L);

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                "/ws/assistant/user?sessionId=2002_123&token=" + accessToken
        );

        assertThrows(Exception.class, () -> channel.writeInbound(request));
    }
}
