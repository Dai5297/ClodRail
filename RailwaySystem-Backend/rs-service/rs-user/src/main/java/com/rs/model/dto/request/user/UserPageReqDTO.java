package com.rs.model.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserPageReqDTO {
    
    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "页大小")
    private Integer pageSize;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "真实姓名")
    private String realName;
}
