package com.dai.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserResetPasswordReqDTO{

    /**
     * 旧密码
     */
    @Schema(description = "旧密码", example = "123456")
    private String oldPassword;

    /**
     * 新密码
     */
    @Schema(description = "新密码", example = "123456")
    private String newPassword;
}
