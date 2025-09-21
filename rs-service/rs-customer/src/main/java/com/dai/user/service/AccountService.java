package com.dai.user.service;


import com.dai.user.model.dto.request.UserRestPasswordReqDTO;

public interface AccountService {

    /**
     * 获取账号密码登录验证码
     * @return 验证码
     */
    String captcha();

    /**
     * 重置密码
     * @param reqDTO 重置密码参数
     */
    void resetPassword(UserRestPasswordReqDTO reqDTO);
}
