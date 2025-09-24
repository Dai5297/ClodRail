package com.dai.controller.user;

import com.dai.model.dto.request.UserNameLoginReqDTO;
import com.dai.model.dto.request.UserRegisterReqDTO;
import com.dai.model.dto.request.UserResetPasswordReqDTO;
import com.dai.model.dto.response.UserLoginResDTO;
import com.dai.model.dto.response.UserRegisterResDTO;
import com.dai.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "账号相关接口")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/login/username")
    @Operation(summary = "用户名密码登录")
    public UserLoginResDTO loginByUserName(@RequestBody UserNameLoginReqDTO reqDTO) {
        return accountService.loginByUserName(reqDTO);
    }

    @PostMapping("/logout")
    @Operation(summary = "登出")
    public void logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        accountService.logout(authorization);
    }

    @GetMapping("/captcha")
    @Operation(summary = "账号密码登录获取验证码")
    public String captcha() {
        return accountService.captcha();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "重置密码")
    public void resetPassword(@RequestBody UserResetPasswordReqDTO reqDTO) {
        accountService.resetPassword(reqDTO);
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    public UserRegisterResDTO register(@RequestBody UserRegisterReqDTO reqDTO) {
        return accountService.register(reqDTO);
    }
}