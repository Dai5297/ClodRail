package com.rs.client.order;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order-service", contextId = "orderClient", path = "/api/order")
public interface OrderClient {

}
