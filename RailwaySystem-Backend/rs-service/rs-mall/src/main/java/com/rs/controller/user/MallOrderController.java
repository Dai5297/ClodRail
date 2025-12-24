package com.rs.controller.user;

import com.rs.model.PageResult;
import com.rs.model.dto.req.ExchangeReqDTO;
import com.rs.model.dto.res.ExchangeResDTO;
import com.rs.model.dto.res.MallOrderResDTO;
import com.rs.service.MallOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商城订单控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "商城订单相关接口")
@RequestMapping("/customer/mall")
public class MallOrderController {

    private final MallOrderService mallOrderService;

    /**
     * 商品兑换
     */
    @PostMapping("/exchange")
    @Operation(summary = "商品兑换", description = "使用积分兑换商品")
    public ExchangeResDTO exchange(@Valid @RequestBody ExchangeReqDTO exchangeReqDTO) {
        return mallOrderService.exchange(exchangeReqDTO);
    }

    /**
     * 查询商城订单
     */
    @GetMapping("/orders")
    @Operation(summary = "查询商城订单", description = "分页查询用户的商城订单")
    public PageResult<MallOrderResDTO> getOrders(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        return mallOrderService.getUserOrders(page, size, status);
    }
}
