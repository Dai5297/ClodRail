package com.rs.controller.inner;

import com.rs.dto.request.mall.AddPointReqDTO;
import com.rs.service.PointService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "内部积分服务")
@RequestMapping("/inner/point")
public class InnerPointController {

    private final PointService pointService;

    @PostMapping("/addPoint")
    void addPoint(@RequestBody AddPointReqDTO pointReqDTO) {
        pointService.addPoint(pointReqDTO);
    }
}
