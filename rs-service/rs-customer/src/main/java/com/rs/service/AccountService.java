package com.rs.service;

import com.rs.model.dto.request.*;
import com.rs.model.dto.response.UserLoginResDTO;
import com.rs.model.dto.response.UserRegisterResDTO;

public interface AccountService {

    /**
     * 账号密码登录
     *
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
     *
     * @return 验证码
     */
    String captcha();

    /**
     * 重置密码
     *
     * @param reqDTO 重置密码参数
     */
    void resetPassword(UserResetPasswordReqDTO reqDTO);

    /**
     * 注册
     *
     * @param reqDTO 注册参数
     * @return 注册结果
     */
    UserRegisterResDTO register(UserRegisterReqDTO reqDTO);

    /**
     * 获取手机验证码
     */
    void captchaPhone(String phone);

    /**
     * 修改手机
     *
     * @param userPhoneChangeReqDTO 修改手机参数
     */
    void changePhone(UserPhoneChangeReqDTO userPhoneChangeReqDTO);

    /**
     * 获取邮箱验证码
     */
    void emailChangeCode(String email);

    /**
     * 修改邮箱
     *
     * @param userEmailChangeReqDTO 修改邮箱参数
     */
    void changeEmail(UserEmailChangeReqDTO userEmailChangeReqDTO);
}