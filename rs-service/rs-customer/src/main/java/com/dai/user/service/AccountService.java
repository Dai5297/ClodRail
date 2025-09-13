package com.dai.user.service;


public interface AccountService {

    /**
     * 获取账号密码登录验证码
     * @return 验证码
     */
    String captcha();
}
