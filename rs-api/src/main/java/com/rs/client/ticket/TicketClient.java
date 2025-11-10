package com.rs.client.ticket;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ticket-service", contextId = "ticketClient", path = "/inner/tickets")
public interface TicketClient {

    @GetMapping("/price")
    Double queryTicketPrice(@RequestParam("ticketId") Long ticketId, @RequestParam("seatType") Integer seatType);
}
