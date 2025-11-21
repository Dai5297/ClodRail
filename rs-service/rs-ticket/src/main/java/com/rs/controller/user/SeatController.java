package com.rs.controller.user;

import com.rs.model.dto.response.AvailableSeatResDTO;
import com.rs.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "座位相关接口")
@RequestMapping("/customer/seats")
public class SeatController {

    private final SeatService seatService;


    @GetMapping("/available")
    @Operation(summary = "获取可用座位")
    public List<AvailableSeatResDTO> availableSeats(
            @RequestParam Long ticketId,
            @RequestParam Integer seatType
    ) {
        return seatService.availableSeats(ticketId, seatType);
    }
}
