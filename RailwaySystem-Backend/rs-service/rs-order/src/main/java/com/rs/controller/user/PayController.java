package com.rs.controller.user;

import com.rs.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "订单支付接口")
@RequestMapping("/customer/pay/ticket")
public class PayController {

    private final PayService payService;

    @PostMapping("/alipay")
    @Operation(summary = "支付宝支付")
    public String payOrder(@RequestParam String orderId) {
        return payService.payOrder(orderId);
    }

    @PostMapping("/alipay/notify")
    @Operation(summary = "支付宝回调")
    public String payNotify(HttpServletRequest request) throws Exception {
        return payService.payNotify(request);
    }
}
