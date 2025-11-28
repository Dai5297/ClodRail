package com.rs.dto.response.user;

import lombok.Data;

import java.util.List;

@Data
public class PermissionResDTO {

    private Long userId;

    private Integer role;

    private String roleName;

    private List<String> apis;

    private List<String> routes;
}
