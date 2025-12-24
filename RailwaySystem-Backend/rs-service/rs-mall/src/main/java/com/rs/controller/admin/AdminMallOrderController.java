package com.rs.controller.admin;

import com.rs.model.PageResult;
import com.rs.model.dto.res.MallOrderResDTO;
import com.rs.service.MallOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "管理端-商城订单管理")
@RequestMapping("/admin/mall/orders")
public class AdminMallOrderController {

    private final MallOrderService mallOrderService;

    @GetMapping("/page")
    @Operation(summary = "商城订单分页查询")
    public PageResult<MallOrderResDTO> page(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return mallOrderService.adminPage(orderNumber, userId, status, pageNum, pageSize);
    }

    @PutMapping("/ship/{orderNumber}")
    @Operation(summary = "商城订单发货")
    public void ship(@PathVariable String orderNumber) {
        mallOrderService.adminShip(orderNumber);
    }
}
