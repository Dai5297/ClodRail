package com.dai.user.model.dto.request;

import lombok.Data;

@Data
public class UserInfoReqDTO {

    /**
     * Id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 地址
     */
    private String address;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 头像
     */
    private String icon;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 身份证号
     */
    private String idCard;
}
