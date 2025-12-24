package com.rs.controller.admin;

import com.rs.model.PageResult;
import com.rs.model.mall.Item;
import com.rs.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "管理端-商城商品管理")
@RequestMapping("/admin/mall/items")
public class AdminItemController {

    private final ItemService itemService;

    @GetMapping("/page")
    @Operation(summary = "商品分页查询")
    public PageResult<Item> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return itemService.adminPage(name, category, status, pageNum, pageSize);
    }

    @PostMapping
    @Operation(summary = "新增商品")
    public void add(@RequestBody Item item) {
        itemService.add(item);
    }

    @PutMapping
    @Operation(summary = "修改商品")
    public void update(@RequestBody Item item) {
        itemService.update(item);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "修改商品状态")
    public void updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        itemService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}

