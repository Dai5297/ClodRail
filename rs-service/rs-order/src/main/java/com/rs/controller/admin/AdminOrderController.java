package com.rs.controller.admin;

import com.rs.model.dto.response.OrderListResDTO;
import com.rs.model.dto.response.OrderDetailResDTO;
import com.rs.model.dto.response.OrderStatisticsResDTO;
import com.rs.model.PageResult;
import com.rs.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "管理端-订单统计")
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("/statistics")
    @Operation(summary = "订单统计")
    public OrderStatisticsResDTO statistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String groupBy) {
        return orderService.statistics(startDate, endDate, groupBy);
    }

    @GetMapping("/recent")
    @Operation(summary = "最近订单列表")
    public List<OrderListResDTO> recent(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        return orderService.recentOrders(limit);
    }

    @GetMapping("/recent/base")
    @Operation(summary = "最近订单列表（基础信息）")
    public List<OrderListResDTO> recentBase(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        return orderService.recentOrdersBase(limit);
    }

    @GetMapping("/page")
    @Operation(summary = "管理端订单分页")
    public PageResult<OrderListResDTO> page(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        return orderService.adminPage(pageNum, pageSize, orderId, status, userId, startTime, endTime);
    }

    @GetMapping("/detail/{orderId}")
    @Operation(summary = "管理端订单详情")
    public OrderDetailResDTO detail(@PathVariable String orderId) {
        return orderService.adminDetail(orderId);
    }

    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "管理端取消订单")
    public void cancel(@PathVariable String orderId) {
        orderService.adminCancel(orderId);
    }

    @PostMapping("/refund/{orderId}")
    @Operation(summary = "管理端退票/退款")
    public void refund(@PathVariable String orderId) {
        orderService.adminRefund(orderId);
    }
}
