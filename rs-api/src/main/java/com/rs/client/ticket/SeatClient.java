package com.rs.client.ticket;

import com.rs.dto.request.FetchSeatReqDTO;
import com.rs.dto.request.OccupySeatReqDTO;
import com.rs.dto.response.FetchSeatResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PostMapping("/occupy")
    boolean preOccupySeat(@RequestBody OccupySeatReqDTO occupySeatReqDTO);

    @PutMapping("/rollback/seat")
    void rollbackOccupySeat(@RequestParam Long orderId);
}
