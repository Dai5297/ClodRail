package com.rs.util;

import cn.hutool.core.lang.UUID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;

import static com.rs.constant.CommonConstant.ACCESS_TOKEN_TYPE;
import static com.rs.constant.CommonConstant.USER_ID_CLAIM;

public class JWTUtil {
    // JWT过期时间
    public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;
    private static volatile SecretKey secretKey;

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成JWT
     * @param subject 用户唯一标识ID
     * @return JWT
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    public static String createAccessToken(Long userId, Long ttlMillis) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID_CLAIM, userId);
        claims.put("tokenType", ACCESS_TOKEN_TYPE);
        return getJwtBuilder(String.valueOf(userId), claims, ttlMillis, getUUID()).compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        return getJwtBuilder(subject, null, ttlMillis, uuid);
    }

    private static JwtBuilder getJwtBuilder(String subject, Map<String, Object> claims, Long ttlMillis, String uuid) {
        // 自行选择加密算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JWTUtil.JWT_TTL;
        }

        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        JwtBuilder builder = Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("sg")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
        if (claims != null && !claims.isEmpty()) {
            builder.addClaims(claims);
        }
        return builder;
    }

    public static SecretKey generalKey() {
        SecretKey currentKey = secretKey;
        if (currentKey == null) {
            throw new IllegalStateException("JWT key has not been configured");
        }
        return currentKey;
    }

    public static void initialize(String jwtKey) {
        if (jwtKey == null || jwtKey.isBlank()) {
            throw new IllegalArgumentException("JWT key must not be blank");
        }
        byte[] keyBytes = jwtKey.getBytes(StandardCharsets.UTF_8);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 校验JWT
     */
    public static Claims parseJWT(String jwt) {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static Long getUserId(Claims claims) {
        Object userId = claims.get(USER_ID_CLAIM);
        if (userId instanceof Number number) {
            return number.longValue();
        }
        if (userId instanceof String userIdStr) {
            return Long.valueOf(userIdStr);
        }
        return null;
    }

    public static boolean isAccessToken(Claims claims) {
        return ACCESS_TOKEN_TYPE.equals(claims.get("tokenType", String.class));
    }
}
