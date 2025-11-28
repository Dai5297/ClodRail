package com.rs.controller.admin;

import com.rs.model.dto.request.AdminProfileReqDTO;
import com.rs.model.dto.response.admin.AdminProfileResDTO;
import com.rs.service.AdminProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "管理员-个人中心")
@RequestMapping("/admin/profile")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    @GetMapping("/info")
    @Operation(summary = "获取管理员信息")
    public AdminProfileResDTO info() {
        return adminProfileService.info();
    }

    @PutMapping("/avatar")
    @Operation(summary = "修改管理员头像")
    public String updateIcon(@RequestParam("file") MultipartFile file) {
        return adminProfileService.updateIcon(file);
    }

    @PutMapping("/info")
    @Operation(summary = "修改管理员信息")
    public void updateInfo(@RequestBody AdminProfileReqDTO reqDTO) {
        adminProfileService.updateInfo(reqDTO);
    }
}
