package com.rs.service;

import com.rs.dto.response.user.PermissionResDTO;

public interface PermissionService {

    /**
     * 查询权限
     *
     * @param id 用户id
     * @return 权限
     */
    PermissionResDTO queryPermissions(Long id);
}
