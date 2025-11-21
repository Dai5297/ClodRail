package com.rs.model.dto.response;

import lombok.Data;

@Data
public class UserRegisterResDTO {
    private Long id;

    private String username;

    private String email;

    private String phone;

    private String realName;

    private String idCard;
}
