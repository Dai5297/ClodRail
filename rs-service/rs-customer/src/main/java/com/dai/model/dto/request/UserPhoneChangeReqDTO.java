package com.dai.model.dto.request;

import lombok.Data;

@Data
public class UserPhoneChangeReqDTO {

    private String newPhone;

    private String code;
}
