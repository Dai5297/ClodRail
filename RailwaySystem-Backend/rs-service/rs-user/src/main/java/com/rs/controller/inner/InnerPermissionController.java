package com.rs.controller.inner;

import com.rs.dto.response.user.PermissionResDTO;
import com.rs.service.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "内部权限接口")
@RequestMapping("/inner/permission")
public class InnerPermissionController {

    private final PermissionService permissionService;

    @GetMapping("/{id}")
    public PermissionResDTO queryUserPermissions(@PathVariable Long id) {
        return permissionService.queryPermissions(id);
    }
}
