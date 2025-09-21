package com.dai.user.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录返回结果
 */
@Data
public class UserLoginResDTO {
    /**
     * 用户id
     */
    @Schema(description = "用户Id", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "dai")
    private String username;

    /**
     * 性别
     */
    @Schema(description = "性别", example = "男")
    private String gender;

    /**
     * 生日
     */
    @Schema(description = "生日", example = "2020-01-01")
    private LocalDateTime birthday;

    /**
     *  地址
     */
    @Schema(description = "地址", example = "中国")
    private String address;

    /**
     *  个性签名
     */
    @Schema(description = "个性签名", example = "程序员")
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
     * 手机号
     */
    @Schema(description = "手机号码", example = "15160255297")
    private String phone;

    /**
     * 登录token
     */
    @Schema(description = "登录token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYwMjMxNjQxNywiZXhwIjoxNjAyMzE5MDE3fQ.XwZjYXZjYX")
    private String token;
}
