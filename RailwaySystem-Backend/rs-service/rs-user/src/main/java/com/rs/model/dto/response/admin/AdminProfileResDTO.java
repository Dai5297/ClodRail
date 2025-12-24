package com.rs.model.dto.response.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminProfileResDTO {

    private Long id;

    private String username;

    private String realName;

    private String idCard;

    private String icon;

    private Integer role;

    private String roleName;

    private Integer status;

    private LocalDateTime createTime;
}
