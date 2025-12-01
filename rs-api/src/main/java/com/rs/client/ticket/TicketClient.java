package com.rs.client.ticket;

import com.rs.dto.request.ticket.AssistantOrderMsgDTO;
import com.rs.dto.response.ticket.ListTicketResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ticket-service", contextId = "ticketClient", path = "/inner/tickets")
public interface TicketClient {

    @GetMapping("/price")
    Double queryTicketPrice(@RequestParam("ticketId") Long ticketId, @RequestParam("seatType") Integer seatType);

    @PostMapping("/list")
    List<ListTicketResDTO> list(@RequestBody List<Long> ticketIds);

    @GetMapping("/order/msg")
    AssistantOrderMsgDTO queryOrderMsgDetail(@RequestParam Long ticketId);
}
