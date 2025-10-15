package com.rs.controller.user;

import com.rs.model.dto.response.StationResDTO;
import com.rs.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "站点管理")
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    @GetMapping
    @Operation(summary = "获取站点列表")
    public List<StationResDTO> stationList() {
        return stationService.stationList();
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门站点")
    public List<StationResDTO> hotStation() {
        return stationService.hotStation();
    }
}
