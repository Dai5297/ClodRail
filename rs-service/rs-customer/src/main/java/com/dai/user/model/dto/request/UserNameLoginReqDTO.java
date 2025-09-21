package com.dai.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "123456")
    private String password;
}
