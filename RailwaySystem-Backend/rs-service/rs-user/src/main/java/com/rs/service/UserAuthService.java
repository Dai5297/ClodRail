package com.rs.service;

import com.rs.model.dto.request.user.*;
import com.rs.model.dto.response.user.UserLoginResDTO;
import com.rs.model.dto.response.user.UserRefreshTokenResDTO;
import com.rs.model.dto.response.user.UserRegisterResDTO;

public interface UserAuthService {

    /**
     * 账号密码登录
     *
     * @param reqDTO 登录参数
     * @return 登录结果
     */
    UserLoginResDTO userLoginByUserName(UserNameLoginReqDTO reqDTO);

    /**
     * 登出
     */
    void userLogout(String authorization);

    /**
     * 刷新 access token
     *
     * @param refreshToken refresh token
     * @return 新的 access token
     */
    UserRefreshTokenResDTO refreshAccessToken(String refreshToken);

    /**
     * 获取账号密码登录验证码
     *
     * @return 验证码
     */
    void captcha();

    /**
     * 重置密码
     *
     * @param reqDTO 重置密码参数
     */
    void userResetPassword(UserResetPasswordReqDTO reqDTO);

    /**
     * 注册
     *
     * @param reqDTO 注册参数
     * @return 注册结果
     */
    UserRegisterResDTO userRegister(UserRegisterReqDTO reqDTO);

    /**
     * 获取手机验证码
     */
    void captchaPhone(String phone);

    /**
     * 修改手机
     *
     * @param userPhoneChangeReqDTO 修改手机参数
     */
    void userChangePhone(UserPhoneChangeReqDTO userPhoneChangeReqDTO);

    /**
     * 获取邮箱验证码
     */
    void emailChangeCode(String email);

    /**
     * 修改邮箱
     *
     * @param userEmailChangeReqDTO 修改邮箱参数
     */
    void userChangeEmail(UserEmailChangeReqDTO userEmailChangeReqDTO);
}
