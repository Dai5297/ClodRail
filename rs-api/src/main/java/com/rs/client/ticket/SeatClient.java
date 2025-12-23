package com.rs.client.ticket;

import com.rs.dto.request.ticket.FetchSeatReqDTO;
import com.rs.dto.response.ticket.FetchSeatResDTO;
import com.rs.dto.response.ticket.SeatTypeInfoResDTO;
import com.rs.model.ticket.Seat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ticket-service", contextId = "seatClient", path = "/inner/seats")
public interface SeatClient {

    /**
     * 获取座位
     *
     * @param reqDTO 请求参数
     * @return 座位id
     */
    @PostMapping("/fetch")
    FetchSeatResDTO fetchSeat(@RequestBody FetchSeatReqDTO reqDTO);

    @PutMapping("/rollback/seat")
    void rollbackOccupySeat(@RequestParam Long orderId);

    @GetMapping("/check/stock")
    boolean checkStock(@RequestParam("ticketId") Long ticketId, @RequestParam("seatType") Integer seatType);

    @GetMapping("/query/seatPosition")
    Seat querySeat(@RequestParam String orderId);

    @GetMapping("/query/byId")
    Seat querySeatById(@RequestParam("seatId") Long seatId);

    @PostMapping("/query/seatPosition")
    List<SeatTypeInfoResDTO> ListSeatQuery(@RequestBody List<String> orderId);
}
