package com.rs.model.dto.request.user;

import lombok.Data;

@Data
public class UserPhoneChangeReqDTO {

    private String newPhone;

    private String code;
}
