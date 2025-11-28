package com.rs.model.dto.response.admin;

import lombok.Data;

import java.util.List;

@Data
public class AdminLoginResDTO {

    private Long id;

    private String username;

    private String realName;

    private String icon;

    private Integer role;

    private String roleName;

    private List<String> routes;

    private String token;
}
