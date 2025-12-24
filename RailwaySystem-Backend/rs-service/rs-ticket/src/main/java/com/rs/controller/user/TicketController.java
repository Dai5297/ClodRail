package com.rs.controller.user;

import com.rs.model.PageResult;
import com.rs.model.dto.response.HotTicketResDTO;
import com.rs.model.dto.response.SearchTicketResDTO;
import com.rs.model.dto.response.TicketDetailResDTO;
import com.rs.service.TickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "车票相关接口")
@RequiredArgsConstructor
@RequestMapping("/customer/tickets")
public class TicketController {

    private final TickerService tickerService;

    @GetMapping("/hot")
    @Operation(summary = "获取最热门的车票信息")
    public List<HotTicketResDTO> hotTicket() {
        return tickerService.hotTicket();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索车票")
    public PageResult<SearchTicketResDTO> searchTicket(
            @RequestParam Long originStationId,
            @RequestParam Long destinationStationId,
            @RequestParam LocalDate departureDate,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        return tickerService.searchTicket(
                originStationId, destinationStationId, departureDate, pageNum, pageSize
        );
    }

    @GetMapping("/detail")
    @Operation(summary = "获取车票详情")
    public TicketDetailResDTO ticketDetail(
            @RequestParam Long ticketId
    ) {
        return tickerService.ticketDetail(ticketId);
    }

}
