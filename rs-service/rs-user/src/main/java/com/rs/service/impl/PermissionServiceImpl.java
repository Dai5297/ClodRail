package com.rs.service.impl;

import com.rs.dto.response.user.PermissionResDTO;
import com.rs.mapper.AdminMapper;
import com.rs.mapper.PermissionMapper;
import com.rs.model.customer.Admin;
import com.rs.model.customer.AdminPermission;
import com.rs.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final AdminMapper adminMapper;

    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResDTO queryPermissions(Long id) {
        Admin admin = adminMapper.queryAdminById(id);
        List<AdminPermission> adminPermission = permissionMapper.queryPermission(admin.getRole());
        PermissionResDTO permissionResDTO = new PermissionResDTO();
        permissionResDTO.setRole(admin.getRole());
        permissionResDTO.setUserId(id);
        permissionResDTO.setRoleName(admin.getRoleName());
        permissionResDTO.setRoutes(adminPermission.stream()
                .filter(permission -> permission.getType() == 1)
                .map(AdminPermission::getPermission)
                .toList()
        );
        permissionResDTO.setApis(adminPermission.stream()
                .filter(permission -> permission.getType() == 2)
                .map(AdminPermission::getPermission)
                .toList()
        );
        return permissionResDTO;
    }
}
