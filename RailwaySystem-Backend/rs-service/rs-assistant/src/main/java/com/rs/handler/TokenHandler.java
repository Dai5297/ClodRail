package com.rs.handler;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.rs.client.user.UserClient;
import com.rs.constant.RedisUserKeyConstant;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.model.customer.Admin;
import com.rs.model.customer.User;
import com.rs.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.rs.constant.CommonConstant.ACCESS_TOKEN_TYPE;
import static com.rs.constant.AttributeKeyConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class TokenHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserClient userClient;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.uri().startsWith("/ws")) {
            ctx.fireChannelRead(request.retain());
            return;
        }
        URI uri = new URI(request.uri());

        String query = uri.getQuery();
        if (query == null) {
            throw new CommonException(RespCode.ERROR, "非法请求");
        }

        String sessionId = null;
        UrlQuery urlQuery = UrlQuery.of(uri.getQuery(), CharsetUtil.CHARSET_UTF_8);
        String token = (String) urlQuery.get("token");
        sessionId = (String) urlQuery.get("sessionId");
        if (ObjectUtil.isEmpty(token) || ObjectUtil.isEmpty(sessionId)) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }
        if (request.uri().startsWith("/ws/assistant/user")) {
            User user = authenticateUser(token, sessionId);
            ctx.channel().attr(USER).set(user);
        } else {
            Admin admin = authenticateAdmin(token);
            ctx.channel().attr(AGENT).set(admin);
            renewRedisToken(RedisUserKeyConstant.ADMIN_LOGIN_TOKEN + token, token);
        }

        ctx.channel().attr(SESSION_ID).set(sessionId);
        ctx.fireChannelRead(request.retain());
    }

    private User authenticateUser(String token, String sessionId) {
        try {
            Claims claims = JWTUtil.parseJWT(token);
            if (!ACCESS_TOKEN_TYPE.equals(claims.get("tokenType", String.class))) {
                throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
            }
            Long userId = JWTUtil.getUserId(claims);
            if (userId == null || !sessionId.startsWith(userId + "_")) {
                throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
            }
            User user = new User();
            user.setId(userId);
            user.setUsername(resolveUsername(userId));
            return user;
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            return authenticateLegacyUser(token, sessionId);
        }
    }

    private User authenticateLegacyUser(String token, String sessionId) {
        String redisToken = stringRedisTemplate.opsForValue().get(RedisUserKeyConstant.USER_LOGIN_TOKEN + token);
        Claims claims = parseClaims(redisToken);
        User user = JSONUtil.toBean(claims.getSubject(), User.class);
        if (user == null || user.getId() == null || !sessionId.startsWith(user.getId() + "_")) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }
        renewRedisToken(RedisUserKeyConstant.USER_LOGIN_TOKEN + token, token);
        return user;
    }

    private Admin authenticateAdmin(String token) {
        String redisToken = stringRedisTemplate.opsForValue().get(RedisUserKeyConstant.ADMIN_LOGIN_TOKEN + token);
        Claims claims = parseClaims(redisToken);
        return JSONUtil.toBean(claims.getSubject(), Admin.class);
    }

    private Claims parseClaims(String token) {
        try {
            Claims claims = JWTUtil.parseJWT(token);
            if (ObjectUtil.isEmpty(claims.getSubject())) {
                throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
            }
            return claims;
        } catch (Exception e) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }
    }

    private void renewRedisToken(String redisKey, String token) {
        Long expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
        if (expire != null && expire > 0 && expire < RedisUserKeyConstant.USER_TOKEN_LEAST_TTL) {
            stringRedisTemplate.expire(redisKey, RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.MILLISECONDS);
            log.debug("用户token续期成功，token: {}", token);
        }
    }

    private String resolveUsername(Long userId) {
        try {
            Map<Long, String> usernames = userClient.usernameList(List.of(userId));
            String username = usernames.get(userId);
            if (ObjectUtil.isNotEmpty(username)) {
                return username;
            }
        } catch (Exception e) {
            log.warn("查询用户昵称失败, userId={}", userId, e);
        }
        return "用户" + userId;
    }
}
