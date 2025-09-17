package com.dai.user.service;


import com.dai.user.model.dto.request.UserInfoReqDTO;
import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.response.UserInfoResDTO;
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
    UserInfoResDTO updateInfo(UserInfoReqDTO reqDTO);
}
