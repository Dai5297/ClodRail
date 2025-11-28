package com.rs.client.user;

import com.rs.dto.response.user.PermissionResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", contextId = "permissionClient", path = "/inner/permission")
public interface PermissionClient {

    @GetMapping("/{id}")
    PermissionResDTO queryUserPermissions(@PathVariable Long id);
}
