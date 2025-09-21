package com.dai.user.controller;

import com.dai.user.model.dto.request.UserRestPasswordReqDTO;
import com.dai.user.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Tag(name = "账户相关接口")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/captcha")
    @Operation(summary = "账号密码登录获取验证码")
    public String captcha() {
        return accountService.captcha();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "重置密码")
    @RequestBody(
            description = "重置密码请求参数",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserRestPasswordReqDTO.class)
            )
    )
    public void resetPassword(@RequestBody UserRestPasswordReqDTO reqDTO) {
        accountService.resetPassword(reqDTO);
    }

}
