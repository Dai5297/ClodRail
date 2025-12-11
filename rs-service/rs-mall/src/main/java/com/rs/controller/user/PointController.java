package com.rs.controller.user;

import com.rs.model.PageResult;
import com.rs.model.dto.res.PointHistoryResDTO;
import com.rs.model.dto.res.PointInfoResDTO;
import com.rs.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "用户积分相关接口")
@RequestMapping("/customer/mall/points")
public class PointController {

    private final PointService pointService;

    @GetMapping("/info")
    @Operation(summary = "获取积分信息")
    public PointInfoResDTO info() {
        return pointService.info();
    }

    @GetMapping("/history")
    @Operation(summary = "获取积分明细")
    public PageResult<PointHistoryResDTO> getHistory(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return pointService.getHistory(page, size, type, startDate, endDate);
    }
}
