package com.rs.model.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户名密码登录请求参数
 */
@Data
public class UserNameLoginReqDTO {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "15160255297")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
}
