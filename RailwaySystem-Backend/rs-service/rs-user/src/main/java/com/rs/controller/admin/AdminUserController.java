package com.rs.controller.admin;

import com.rs.model.PageResult;
import com.rs.model.dto.request.user.UserPageReqDTO;
import com.rs.model.dto.response.user.UserInfoResDTO;
import com.rs.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "后台用户管理接口")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/page")
    @Operation(summary = "分页查询用户")
    public PageResult<UserInfoResDTO> page(UserPageReqDTO reqDTO) {
        return userService.page(reqDTO);
    }

    @PostMapping("/username/list")
    @Operation(summary = "批量查询用户名")
    public Map<Long, String> usernameList(@RequestBody List<Long> userIds) {
        return userService.usernameList(userIds);
    }
}
