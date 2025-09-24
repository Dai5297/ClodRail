package com.dai.service;

import com.dai.model.dto.request.UserNameLoginReqDTO;
import com.dai.model.dto.request.UserRegisterReqDTO;
import com.dai.model.dto.request.UserResetPasswordReqDTO;
import com.dai.model.dto.response.UserLoginResDTO;
import com.dai.model.dto.response.UserRegisterResDTO;

public interface AccountService {

    /**
     * 账号密码登录
     * @param reqDTO 登录参数
     * @return 登录结果
     */
    UserLoginResDTO loginByUserName(UserNameLoginReqDTO reqDTO);

    /**
     * 登出
     */
    void logout(String authorization);

    /**
     * 获取账号密码登录验证码
     * @return 验证码
     */
    String captcha();

    /**
     * 重置密码
     * @param reqDTO 重置密码参数
     */
    void resetPassword(UserResetPasswordReqDTO reqDTO);

    /**
     * 注册
     * @param reqDTO 注册参数
     * @return 注册结果
     */
    UserRegisterResDTO register(UserRegisterReqDTO reqDTO);
}