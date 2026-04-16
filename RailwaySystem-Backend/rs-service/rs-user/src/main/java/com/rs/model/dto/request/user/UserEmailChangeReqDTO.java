package com.rs.model.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserEmailChangeReqDTO {

    /**
     * 新邮箱地址
     */
    @Schema(description = "新邮箱地址", example = "newemail@example.com")
    @NotBlank(message = "新邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String newEmail;

    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "123456")
    @NotBlank(message = "验证码不能为空")
    private String code;
}
