package com.rs.client.order;

import com.rs.model.order.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", contextId = "commonOrderClient", path = "/common/orders")
public interface CommonClient {

    @PostMapping("/assistant/generate")
    String generateAssistantOrder(@RequestBody Order order);

    @GetMapping("/assistant/prase")
    Order praseAssistantOrder(@RequestParam String uuid);
}
