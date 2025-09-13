package com.dai.user.controller;

import com.dai.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.RespResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/captcha")
    public RespResult<String> captcha() {
        return RespResult.ok(accountService.captcha());
    }

}
