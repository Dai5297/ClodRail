package com.dai.user.model.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import model.BaseModel;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
