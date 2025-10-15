package com.rs.controller.inner;

import com.rs.service.SeatService;
import dto.request.FetchSeatReqDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "内部接口-座位相关接口")
@RequestMapping("/inner/seats")
public class InnerSeatController {

    private final SeatService seatService;

    @PostMapping("/fetch")
    Long fetchSeat(@RequestBody FetchSeatReqDTO reqDTO) {
        return seatService.fetchSeat(reqDTO);
    }
}
