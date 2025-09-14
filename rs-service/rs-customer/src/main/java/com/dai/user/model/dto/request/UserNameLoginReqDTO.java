package com.dai.user.model.dto.request;

import lombok.Data;

/**
 * 用户名密码登录请求参数
 */
@Data
public class UserNameLoginReqDTO {

    /**
     * 手机号或邮箱
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
