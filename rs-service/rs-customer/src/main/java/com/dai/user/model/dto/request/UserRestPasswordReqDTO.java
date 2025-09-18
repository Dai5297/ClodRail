package com.dai.user.model.dto.request;

import lombok.Data;

@Data
public class UserRestPasswordReqDTO {

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}
