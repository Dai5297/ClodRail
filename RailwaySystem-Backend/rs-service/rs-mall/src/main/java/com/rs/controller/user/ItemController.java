package com.rs.controller.user;

import com.rs.model.PageResult;
import com.rs.model.mall.Item;
import com.rs.service.ItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "用户商品相关接口")
@RequestMapping("/customer/mall/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    private PageResult<Item> page(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "isAsc", required = false) Boolean isAsc,
            @RequestParam(value = "category", required = false) String category
    ) {
        return itemService.page(page, size, sortBy, isAsc, category);
    }

    @GetMapping("/categories")
    private List<String> getCategories() {
        return itemService.getCategories();
    }

    @GetMapping("/search")
    private PageResult<Item> search(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "isAsc", required = false) Boolean isAsc,
            @RequestParam(value = "name") String keyword
    ) {
        return itemService.search(page, size, sortBy, isAsc, keyword);
    }
}
