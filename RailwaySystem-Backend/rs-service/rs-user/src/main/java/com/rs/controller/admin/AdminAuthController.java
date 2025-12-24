package com.rs.controller.admin;

import com.rs.model.dto.request.admin.AdminLoginReqDTO;
import com.rs.model.dto.response.admin.AdminLoginResDTO;
import com.rs.service.AdminAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.rs.constant.CommonConstant.AUTHENTICATION;

@RestController
@RequiredArgsConstructor
@Tag(name = "管理员认证相关接口")
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public AdminLoginResDTO login(@RequestBody AdminLoginReqDTO reqDTO) {
        return adminAuthService.login(reqDTO);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(AUTHENTICATION) String token) {
        adminAuthService.logout(token);
    }

}
