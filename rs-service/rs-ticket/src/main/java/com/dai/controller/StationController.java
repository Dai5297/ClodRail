package com.dai.controller;

import com.dai.model.dto.response.StationResDTO;
import com.dai.service.StationService;
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
    public List<StationResDTO> stationList() {
        return stationService.stationList();
    }
}
