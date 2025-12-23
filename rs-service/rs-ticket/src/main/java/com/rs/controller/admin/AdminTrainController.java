package com.rs.controller.admin;

import com.rs.model.PageResult;
import com.rs.model.ticket.Train;
import com.rs.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "管理端-列车管理")
@RequiredArgsConstructor
@RequestMapping("/admin/trains")
public class AdminTrainController {

    private final TrainService trainService;

    @GetMapping("/page")
    @Operation(summary = "列车分页查询")
    public PageResult<Train> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return trainService.adminPage(name, type, status, pageNum, pageSize);
    }

    @PostMapping
    @Operation(summary = "新增列车")
    public void add(@RequestBody Train train) {
        trainService.add(train);
    }

    @PutMapping
    @Operation(summary = "修改列车")
    public void update(@RequestBody Train train) {
        trainService.update(train);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除列车")
    public void delete(@PathVariable Long id) {
        trainService.delete(id);
    }
}
