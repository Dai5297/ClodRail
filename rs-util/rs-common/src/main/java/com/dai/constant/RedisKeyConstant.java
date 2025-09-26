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

    /**
     * 用户注册验证码Key
     */
    public static final String USER_PHONE_CAPTCHA = "user:phone:captcha:";

    /**
     * 用户注册验证码TTL
     */
    public static final long USER_PHONE_CAPTCHA_TTL = 60;

    /**
     * 用户修改邮箱验证码Key
     */
    public static final String USER_EMAIL_CODE = "user:email:code:";

    /**
     * 用户修改邮箱验证码Key
     */
    public static final long USER_EMAIL_CHANGE_CODE_TTL = 300;
}
