package com.rs.model.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserEmailChangeReqDTO {

    /**
     * 新邮箱地址
     */
    @Schema(description = "新邮箱地址", example = "newemail@example.com")
    private String newEmail;

    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "123456")
    private String code;
}