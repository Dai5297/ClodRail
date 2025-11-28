package com.rs.handler;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
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
import java.util.concurrent.TimeUnit;

import static com.rs.constant.AttributeKeyConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class TokenHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final StringRedisTemplate stringRedisTemplate;

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

        String uuid = null;
        String sessionId = null;
        UrlQuery urlQuery = UrlQuery.of(uri.getQuery(), CharsetUtil.CHARSET_UTF_8);
        uuid = (String) urlQuery.get("token");
        sessionId = (String) urlQuery.get("sessionId");

        String key = null;
        if (request.uri().contains("user")) {
            key = RedisUserKeyConstant.USER_LOGIN_TOKEN + uuid;
        }else {
            key = RedisUserKeyConstant.ADMIN_LOGIN_TOKEN + uuid;
        }
        String token = stringRedisTemplate.opsForValue().get(key);
        Claims claims = null;
        try {
            claims = JWTUtil.parseJWT(token);
        } catch (Exception e) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }
        String claimsSubject = claims.getSubject();
        if (ObjectUtil.isEmpty(claimsSubject)) {
            throw new CommonException(RespCode.UNAUTHORIZED, "用户未登录");
        }

        User user = null;
        Admin admin = null;
        if (request.uri().startsWith("/ws/assistant/user")) {
            user = JSONUtil.toBean(claimsSubject, User.class);
            ctx.channel().attr(USER).set(user);
        }else {
            admin = JSONUtil.toBean(claimsSubject, Admin.class);
            ctx.channel().attr(AGENT).set(admin);
        }

        ctx.channel().attr(SESSION_ID).set(sessionId);

        // 续期redisKey
        Long expire = stringRedisTemplate.getExpire(RedisUserKeyConstant.USER_LOGIN_TOKEN + uuid, TimeUnit.SECONDS);

        if (expire != null && expire > 0 && expire < RedisUserKeyConstant.USER_TOKEN_LEAST_TTL) {
            // 使用秒为单位，与getExpire保持一致
            stringRedisTemplate.expire(RedisUserKeyConstant.USER_LOGIN_TOKEN + uuid,
                    RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.SECONDS);
            log.debug("用户token续期成功，uuid: {}", uuid);
        }

        ctx.fireChannelRead(request.retain());
    }
}
