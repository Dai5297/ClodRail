package com.dai.user.service;


import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.response.UserLoginResDTO;

public interface AccountService {

    /**
     * 获取账号密码登录验证码
     * @return 验证码
     */
    String captcha();

    /**
     * 账号密码登录
     * @param reqDTO 登录参数
     * @return 登录结果
     */
    UserLoginResDTO loginByUserName(UserNameLoginReqDTO reqDTO);
}
