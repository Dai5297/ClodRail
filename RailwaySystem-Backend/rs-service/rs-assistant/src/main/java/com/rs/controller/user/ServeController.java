package com.rs.controller.user;

import com.rs.service.ServeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "客服服务相关接口")
@RequestMapping("/customer/assistant/service")
public class ServeController {

    private final ServeService serveService;

    @GetMapping("/status")
    public Integer status() {
        return serveService.status();
    }
}
