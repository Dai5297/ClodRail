package com.dai.user.controller;

import com.dai.user.model.dto.request.UserInfoReqDTO;
import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.response.UserInfoResDTO;
import com.dai.user.model.dto.response.UserLoginResDTO;
import com.dai.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    @RequestBody(
            description = "用户名密码登录请求参数",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserNameLoginReqDTO.class)
            )
    )
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
    @RequestBody(
            description = "更新用户信息请求参数",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserInfoReqDTO.class)
            )
    )
    public UserInfoResDTO updateInfo(@RequestBody UserInfoReqDTO reqDTO) {
        return userService.updateInfo(reqDTO);
    }


}
