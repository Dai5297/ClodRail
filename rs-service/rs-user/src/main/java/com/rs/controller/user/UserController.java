package com.rs.controller.user;

import com.rs.model.dto.request.user.UserInfoUpdateReqDTO;
import com.rs.model.dto.response.user.UserInfoResDTO;
import com.rs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/user")
@RequiredArgsConstructor
@Tag(name = "用户信息相关接口")
public class UserController {

    private final UserService userService;

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
}
