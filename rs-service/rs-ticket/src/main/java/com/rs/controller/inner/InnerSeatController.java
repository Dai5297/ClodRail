package com.rs.controller.inner;

import com.rs.dto.request.ticket.FetchSeatReqDTO;
import com.rs.dto.response.ticket.FetchSeatResDTO;
import com.rs.dto.response.ticket.SeatTypeInfoResDTO;
import com.rs.model.ticket.Seat;
import com.rs.service.SeatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "内部接口-座位相关接口")
@RequestMapping("/inner/seats")
public class InnerSeatController {

    private final SeatService seatService;

    @PostMapping("/fetch")
    FetchSeatResDTO fetchSeat(@RequestBody FetchSeatReqDTO reqDTO) {
        return seatService.fetchSeat(reqDTO);
    }

    @PutMapping("/rollback/seat")
    void rollbackOccupySeat(@RequestParam Long orderId) {
        seatService.rollbackOccupySeat(orderId);
    }

    @GetMapping("/check/stock")
    boolean checkStock(@RequestParam("ticketId") Long ticketId, @RequestParam("seatType") Integer seatType){
        return seatService.checkStock(ticketId, seatType);
    }

    @GetMapping("/query/seatPosition")
    Seat querySeat(@RequestParam String orderId) {
        return seatService.querySeat(orderId);
    }

    @PostMapping("/query/seatPosition")
    List<SeatTypeInfoResDTO> ListSeatQuery(@RequestBody List<String> orderIds) {
        return seatService.ListSeatQuery(orderIds);
    }
}
