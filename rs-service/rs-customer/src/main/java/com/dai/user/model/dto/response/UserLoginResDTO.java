package com.dai.user.model.dto.response;

import lombok.Data;

/**
 * 登录返回结果
 */
@Data
public class UserLoginResDTO {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String icon;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;
}
