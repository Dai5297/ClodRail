package com.dai.user.controller;

import com.dai.user.model.dto.request.UserInfoReqDTO;
import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.request.UserRestPasswordReqDTO;
import com.dai.user.model.dto.response.UserInfoResDTO;
import com.dai.user.model.dto.response.UserLoginResDTO;
import com.dai.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/captcha")
    public String captcha() {
        return accountService.captcha();
    }

    @PostMapping("/login/username")
    public UserLoginResDTO loginByUserName(@RequestBody UserNameLoginReqDTO reqDTO) {
        return accountService.loginByUserName(reqDTO);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        accountService.logout(authorization);
    }

    @GetMapping("/info")
    public UserInfoResDTO info() {
        return accountService.info();
    }

    @PostMapping("/info/update")
    public UserInfoResDTO updateInfo(@RequestBody UserInfoReqDTO reqDTO) {
        return accountService.updateInfo(reqDTO);
    }

    @PostMapping("/password/reset")
    public void resetPassword(@RequestBody UserRestPasswordReqDTO reqDTO) {
        accountService.resetPassword(reqDTO);
    }

}
