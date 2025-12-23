package com.rs.controller.admin;

import com.rs.model.PageResult;
import com.rs.model.dto.request.admin.AdminSaveReqDTO;
import com.rs.model.dto.response.admin.AdminProfileResDTO;
import com.rs.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "管理端-管理员管理")
@RequestMapping("/admin/admins")
public class AdminManageController {

    private final AdminService adminService;

    @GetMapping("/page")
    @Operation(summary = "管理员分页查询")
    public PageResult<AdminProfileResDTO> page(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return adminService.adminPage(username, realName, status, pageNum, pageSize);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "修改管理员状态")
    public void updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateStatus(id, status);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "修改管理员角色")
    public void updateRole(@PathVariable Long id, @RequestParam Integer role) {
        adminService.updateRole(id, role);
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置管理员密码")
    public void resetPassword(@PathVariable Long id) {
        adminService.resetPassword(id);
    }

    @PostMapping
    @Operation(summary = "新增管理员")
    public void add(@RequestBody AdminSaveReqDTO reqDTO) {
        adminService.save(reqDTO);
    }

    @PutMapping
    @Operation(summary = "编辑管理员")
    public void update(@RequestBody AdminSaveReqDTO reqDTO) {
        adminService.save(reqDTO);
    }
}
