package com.rs.model.dto.request.admin;

import lombok.Data;

@Data
public class AdminSaveReqDTO {

    private Long id;

    private String username;

    private String realName;

    private String idCard;

    private Integer role;

    private Integer status;
}
