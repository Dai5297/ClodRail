package com.rs.controller.inner;

import com.rs.annotation.IgnoreResult;
import com.rs.dto.request.ticket.AssistantOrderMsgDTO;
import com.rs.dto.response.ticket.ListTicketResDTO;
import com.rs.service.TickerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "内部接口-票务相关接口")
@RequestMapping("/inner/tickets")
public class InnerTicketController {

    private final TickerService tickerService;

    @IgnoreResult
    @GetMapping("/price")
    Double queryTicketPrice(@RequestParam("ticketId") Long ticketId, @RequestParam("seatType") Integer seatType) {
        return tickerService.queryTicketPrice(ticketId, seatType);
    }

    @IgnoreResult
    @PostMapping("/list")
    List<ListTicketResDTO> list(@RequestBody List<Long> ticketIds) {
        return tickerService.list(ticketIds);
    }

    @IgnoreResult
    @GetMapping("/order/msg")
    AssistantOrderMsgDTO queryOrderMsgDetail(@RequestParam Long ticketId) {
        return tickerService.queryOrderMsgDetail(ticketId);
    }
}
