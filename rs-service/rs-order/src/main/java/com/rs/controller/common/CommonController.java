package com.rs.controller.common;

import com.rs.model.order.Order;
import com.rs.service.CommonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "公共订单接口接口")
@RequestMapping("/common/order")
public class CommonController {

    private final CommonService commonService;

    @PostMapping("/assistant/generate")
    public String generateAssistantOrder(@RequestBody Order order) {
        return commonService.generateAssistantOrder(order);
    }

    @GetMapping("/assistant/prase")
    public Order praseAssistantOrder(@RequestParam String uuid) {
        return commonService.praseAssistantOrder(uuid);
    }
}
