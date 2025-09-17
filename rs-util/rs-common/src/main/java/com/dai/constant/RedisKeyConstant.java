package com.dai.constant;

public class RedisKeyConstant {

    /**
     * 用户登录tokenKey
     */
    public static final String USER_LOGIN_TOKEN = "user:login:token:";

    /**
     * 用户登录tokenTTL
     */
    public static final Long USER_LOGIN_TOKEN_TTL = 3600000L;

    /**
     * 用户登录时间Key
     */
    public static final String USER_LOGIN_TIME = "user:login:time:";

    /**
     * 用户登录token剩余TTL
     */
    public static final Long USER_TOKEN_LEAST_TTL = 600000L;

}
