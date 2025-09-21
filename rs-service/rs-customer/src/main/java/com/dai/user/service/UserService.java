package com.dai.user.service;

import com.dai.user.model.dto.request.UserInfoUpdateReqDTO;
import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.request.UserResetPasswordReqDTO;
import com.dai.user.model.dto.response.UserInfoResDTO;
import com.dai.user.model.dto.response.UserLoginResDTO;

public interface UserService {

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
     * 获取用户信息
     * @return 用户信息
     */
    UserInfoResDTO info();

    /**
     * 修改用户信息
     * @param reqDTO 修改参数
     * @return 修改结果
     */
    UserInfoResDTO updateInfo(UserInfoUpdateReqDTO reqDTO);

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
}
