package com.rs.model.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserResetPasswordReqDTO{

    /**
     * 旧密码
     */
    @Schema(description = "旧密码", example = "123456")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @Schema(description = "新密码", example = "123456")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
