package com.rs.controller.inner;

import com.rs.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "内部用户接口")
@RequestMapping("/inner/user")
public class InnerUserController {

    private final UserService userService;
    @PostMapping("/username/list")
    Map<Long, String> usernameList(@RequestBody List<Long> userIds) {
        return userService.usernameList(userIds);
    }
}
