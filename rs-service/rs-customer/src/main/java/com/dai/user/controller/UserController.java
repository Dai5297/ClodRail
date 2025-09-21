package com.dai.user.controller;

import com.dai.user.model.dto.request.UserInfoUpdateReqDTO;
import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.request.UserResetPasswordReqDTO;
import com.dai.user.model.dto.response.UserInfoResDTO;
import com.dai.user.model.dto.response.UserLoginResDTO;
import com.dai.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户相关接口")
public class UserController {

    private final UserService userService;

    @PostMapping("/login/username")
    @Operation(summary = "用户名密码登录")
    public UserLoginResDTO loginByUserName(@RequestBody UserNameLoginReqDTO reqDTO) {
        return userService.loginByUserName(reqDTO);
    }

    @PostMapping("/logout")
    @Operation(summary = "登出")
    public void logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        userService.logout(authorization);
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public UserInfoResDTO info() {
        return userService.info();
    }

    @PostMapping("/info/update")
    @Operation(summary = "更新用户信息")
    public UserInfoResDTO updateInfo(@RequestBody UserInfoUpdateReqDTO reqDTO) {
        return userService.updateInfo(reqDTO);
    }

    @GetMapping("/captcha")
    @Operation(summary = "账号密码登录获取验证码")
    public String captcha() {
        return userService.captcha();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "重置密码")
    public void resetPassword(@RequestBody UserResetPasswordReqDTO reqDTO) {
        userService.resetPassword(reqDTO);
    }
}
