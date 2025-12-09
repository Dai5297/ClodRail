package com.rs.controller.user;

import com.rs.model.dto.res.PointInfoResDTO;
import com.rs.service.PointService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "用户积分相关接口")
@RequestMapping("/customer/mall/points")
public class PointController {

    private final PointService pointService;

    @GetMapping("/info")
    public PointInfoResDTO info() {
        return pointService.info();
    }
}
