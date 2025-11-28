package com.rs.model.customer;

import lombok.Data;

@Data
public class AdminPermission {

    private Long id;

    private String permission;

    private Integer type;

    private Integer role;

    private String roleName;

    private String description;
}
