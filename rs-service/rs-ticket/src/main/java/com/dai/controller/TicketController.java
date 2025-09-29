package com.dai.controller;

import com.dai.model.PageResult;
import com.dai.model.dto.request.SearchTicketReqDTO;
import com.dai.model.dto.response.HotTicketResDTO;
import com.dai.model.dto.response.SearchTicketResDTO;
import com.dai.service.TickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "车票相关接口")
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {

    private final TickerService tickerService;

    @GetMapping("/hot")
    @Operation(summary = "获取最热门的车票信息")
    public List<HotTicketResDTO> hotTicket() {
        return tickerService.hotTicket();
    }

    @PostMapping("/search")
    @Operation(summary = "搜索车票")
    public PageResult<SearchTicketResDTO> searchTicket(@RequestBody SearchTicketReqDTO reqDTO) {
        return tickerService.searchTicket(reqDTO);
    }

}
