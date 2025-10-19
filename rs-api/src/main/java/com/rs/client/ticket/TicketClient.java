package com.rs.client.ticket;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ticket-service", contextId = "ticketClient", path = "/inner/tickets")
public interface TicketClient {
}
