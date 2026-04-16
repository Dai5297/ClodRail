package com.rs.model.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRefreshTokenResDTO {

    @Schema(description = "access token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjAyMzE2NDE3LCJleHAiOjE2MDIzMTkwMTd9.XwZjYXZjYX")
    private String accessToken;

    @Schema(description = "token 类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "access token 过期秒数", example = "900")
    private Long expiresIn;
}
