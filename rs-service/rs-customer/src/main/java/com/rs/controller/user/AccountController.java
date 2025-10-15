package com.rs.controller.user;

import com.rs.model.dto.request.*;
import com.rs.model.dto.response.UserLoginResDTO;
import com.rs.model.dto.response.UserRegisterResDTO;
import com.rs.service.AccountService;
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

    @GetMapping("/captcha/phone")
    @Operation(summary = "手机验证码登录获取验证码")
    public void captchaPhone(@RequestParam String phone) {
        accountService.captchaPhone(phone);
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

    @PostMapping("/phone/change")
    @Operation(summary = "更换手机号")
    public void changePhone(@RequestBody UserPhoneChangeReqDTO userPhoneChangeReqDTO) {
        accountService.changePhone(userPhoneChangeReqDTO);
    }

    @PostMapping("/email/change/code")
    @Operation(summary = "获取更换邮箱验证码")
    public void emailChangeCode(@RequestParam String email) {
        accountService.emailChangeCode(email);
    }

    @PostMapping("/email/change")
    @Operation(summary = "更换邮箱")
    public void changeEmail(@RequestBody UserEmailChangeReqDTO userEmailChangeReqDTO) {
        accountService.changeEmail(userEmailChangeReqDTO);
    }
}