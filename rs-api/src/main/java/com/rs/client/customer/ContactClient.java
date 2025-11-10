package com.rs.client.customer;

import com.rs.dto.response.customer.PassengerResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "customer-service", contextId = "contactClient", path = "/inner/contact")
public interface ContactClient {

    @GetMapping("/query/passenger")
    List<PassengerResDTO> queryPassenger(@RequestParam List<Long> passengerIds);
}
