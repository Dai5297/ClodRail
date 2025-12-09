package com.rs.controller.user;

import com.rs.model.PageResult;
import com.rs.model.dto.request.OrderCreateReqDTO;
import com.rs.model.dto.response.OrderCreateResDTO;
import com.rs.model.dto.response.OrderDetailResDTO;
import com.rs.model.dto.response.OrderListResDTO;
import com.rs.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "订单相关接口")
@RequestMapping("/customer/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    @GlobalTransactional
    public OrderCreateResDTO createOrder(@RequestBody OrderCreateReqDTO reqDTO) {
        return orderService.createOrder(null, reqDTO);
    }

    @GetMapping("/detail/{orderId}")
    public OrderDetailResDTO orderDetail(@PathVariable String orderId) {
        return orderService.orderDetail(orderId);
    }

    @GetMapping("/page")
    public PageResult<OrderListResDTO> list(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) Integer status
    ) {
        return orderService.list(pageNum, pageSize, orderId, status);
    }
}
