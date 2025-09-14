package com.dai.user.controller;

import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.response.UserLoginResDTO;
import com.dai.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.dai.model.RespResult;

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

}
