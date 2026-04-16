package com.rs.service.impl;

import com.rs.mapper.UserAuthMapper;
import com.rs.mapper.UserMapper;
import com.rs.model.customer.User;
import com.rs.model.dto.request.user.UserNameLoginReqDTO;
import com.rs.model.dto.response.user.UserLoginResDTO;
import com.rs.model.dto.response.user.UserRefreshTokenResDTO;
import com.rs.util.EncoderUtil;
import com.rs.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static com.rs.constant.RedisUserKeyConstant.USER_ACCESS_TOKEN_TTL;
import static com.rs.constant.RedisUserKeyConstant.USER_LOGIN_TIME;
import static com.rs.constant.RedisUserKeyConstant.USER_REFRESH_TOKEN;
import static com.rs.constant.RedisUserKeyConstant.USER_REFRESH_TOKEN_TTL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceImplTest {

    @Mock
    private UserAuthMapper userAuthMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UserAuthServiceImpl userAuthService;

    @Captor
    private ArgumentCaptor<String> redisKeyCaptor;

    @Captor
    private ArgumentCaptor<String> redisValueCaptor;

    private User user;

    @BeforeEach
    void setUp() {
        JWTUtil.initialize("test-jwt-key-1234567890-abcdef123456");
        user = new User();
        user.setId(1001L);
        user.setUsername("tester");
        user.setPassword(EncoderUtil.encrypt("password123"));
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void userLoginShouldReturnAccessTokenAndPersistRefreshToken() {
        UserNameLoginReqDTO reqDTO = new UserNameLoginReqDTO();
        reqDTO.setUsername("tester");
        reqDTO.setPassword("password123");

        when(userAuthMapper.findByUsername("tester")).thenReturn(user);

        UserLoginResDTO result = userAuthService.userLoginByUserName(reqDTO);

        assertNotNull(result.getAccessToken());
        assertEquals(result.getAccessToken(), result.getToken());
        assertNotNull(result.getRefreshToken());
        assertEquals(TimeUnit.MILLISECONDS.toSeconds(USER_ACCESS_TOKEN_TTL), result.getExpiresIn());

        verify(valueOperations, atLeastOnce()).set(redisKeyCaptor.capture(), redisValueCaptor.capture(), anyLong(), any(TimeUnit.class));
        assertTrue(redisKeyCaptor.getAllValues().stream().anyMatch(key -> key.startsWith(USER_REFRESH_TOKEN)));
        assertTrue(redisValueCaptor.getAllValues().contains(String.valueOf(user.getId())));
        verify(valueOperations).set(startsWith(USER_REFRESH_TOKEN), eq(String.valueOf(user.getId())), eq(USER_REFRESH_TOKEN_TTL), eq(TimeUnit.MILLISECONDS));
        verify(valueOperations).set(startsWith(USER_LOGIN_TIME), anyString());
    }

    @Test
    void refreshShouldIssueNewAccessTokenWhenRefreshTokenIsValid() {
        when(valueOperations.get(USER_REFRESH_TOKEN + "refresh-token")).thenReturn("1001");
        when(userMapper.queryById(1001L)).thenReturn(user);

        UserRefreshTokenResDTO result = userAuthService.refreshAccessToken("refresh-token");

        assertNotNull(result.getAccessToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(TimeUnit.MILLISECONDS.toSeconds(USER_ACCESS_TOKEN_TTL), result.getExpiresIn());
        verify(stringRedisTemplate).expire(USER_REFRESH_TOKEN + "refresh-token", USER_REFRESH_TOKEN_TTL, TimeUnit.MILLISECONDS);
    }
}
