package com.rs.controller.admin;

import com.rs.model.PageResult;
import com.rs.model.ticket.Station;
import com.rs.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "管理端-车站管理")
@RequiredArgsConstructor
@RequestMapping("/admin/stations")
public class AdminStationController {

    private final StationService stationService;

    @GetMapping("/page")
    @Operation(summary = "车站分页查询")
    public PageResult<Station> page(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return stationService.adminPage(name, pageNum, pageSize);
    }

    @PostMapping
    @Operation(summary = "新增车站")
    public void add(@RequestBody Station station) {
        stationService.add(station);
    }

    @PutMapping
    @Operation(summary = "修改车站")
    public void update(@RequestBody Station station) {
        stationService.update(station);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车站")
    public void delete(@PathVariable Long id) {
        stationService.delete(id);
    }
}
