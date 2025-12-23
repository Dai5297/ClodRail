package com.rs.controller.admin;

import com.rs.model.PageResult;
import com.rs.model.dto.request.LineFormDTO;
import com.rs.model.dto.response.StationResDTO;
import com.rs.model.ticket.Line;
import com.rs.service.LineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "管理端-线路管理")
@RequiredArgsConstructor
@RequestMapping("/admin/lines")
public class AdminLineController {

    private final LineService lineService;

    @GetMapping("/page")
    @Operation(summary = "线路分页查询")
    public PageResult<Line> page(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return lineService.adminPage(name, pageNum, pageSize);
    }

    @PostMapping
    @Operation(summary = "新增线路")
    public void add(@RequestBody LineFormDTO dto) {
        lineService.add(dto);
    }

    @PutMapping
    @Operation(summary = "修改线路")
    public void update(@RequestBody LineFormDTO dto) {
        lineService.update(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除线路")
    public void delete(@PathVariable Long id) {
        lineService.delete(id);
    }

    @GetMapping("/{id}/stations")
    @Operation(summary = "获取线路经过的站点")
    public java.util.List<StationResDTO> stations(@PathVariable Long id) {
        return lineService.listStations(id);
    }
}
