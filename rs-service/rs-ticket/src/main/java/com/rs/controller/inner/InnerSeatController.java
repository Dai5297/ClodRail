package com.rs.controller.inner;

import com.rs.dto.request.OccupySeatReqDTO;
import com.rs.dto.response.FetchSeatResDTO;
import com.rs.service.SeatService;
import com.rs.dto.request.FetchSeatReqDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/occupy")
    boolean preOccupySeat(@RequestBody OccupySeatReqDTO occupySeatReqDTO) {
        return seatService.preOccupySeat(occupySeatReqDTO);
    }

    @PutMapping("/rollback/seat")
    void rollbackOccupySeat(@RequestParam Long orderId) {
        seatService.rollbackOccupySeat(orderId);
    }
}
