package com.rs.model.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoUpdateReqDTO {

    /**
     * Id
     */
    @Schema(description = "用户Id", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "dai")
    private String username;

    /**
     * 生日
     */
    @Schema(description = "生日", example = "2020-01-01")
    private String birthday;

    /**
     * 地址
     */
    @Schema(description = "地址", example = "中国")
    private String address;

    /**
     * 个人简介
     */
    @Schema(description = "个人简介", example = "程序员")
    private String introduction;

    /**
     * 头像
     */
    @Schema(description = "头像", example = "https://www.baidu.com")
    private String icon;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "dai5297@163.com")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "15160255297")
    private String phone;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "戴")
    private String realName;

    /**
     * 性别
     */
    @Schema(description = "性别", example = "男")
    private String gender;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;
}
