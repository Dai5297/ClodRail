package com.rs.model.dto.request;

import lombok.Data;

@Data
public class UserRegisterReqDTO {

    private String username;

    private String password;

    private String email;

    private String phone;

    private String realName;

    private String idCard;

    private String code;
}
