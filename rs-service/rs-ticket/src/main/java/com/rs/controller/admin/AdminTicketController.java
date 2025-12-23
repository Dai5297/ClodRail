package com.rs.controller.admin;

import com.rs.model.dto.request.TicketFormDTO;
import com.rs.model.dto.response.SearchTicketResDTO;
import com.rs.model.PageResult;
import com.rs.dto.response.ticket.ListTicketResDTO;
import com.rs.model.ticket.Ticket;
import com.rs.model.ticket.Line;
import com.rs.model.domain.TrainInfo;
import com.rs.service.TickerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController("AdminTicketController")
@RequestMapping("/admin/tickets")
@Tag(name = "后台车票管理")
@RequiredArgsConstructor
public class AdminTicketController {

    private final TickerService tickerService;

    @GetMapping("/{id}")
    @Operation(summary = "获取车票详情")
    public Ticket getTicket(@PathVariable Long id) {
        return tickerService.getTicketById(id);
    }

    @PostMapping
    @Operation(summary = "新增车票")
    public void addTicket(@RequestBody TicketFormDTO dto) {
        tickerService.addTicket(dto);
    }

    @PutMapping
    @Operation(summary = "修改车票")
    public void updateTicket(@RequestBody TicketFormDTO dto) {
        tickerService.updateTicket(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车票")
    public void deleteTicket(@PathVariable Long id) {
        tickerService.deleteTicket(id);
    }

    @GetMapping
    @Operation(summary = "搜索车票")
    public PageResult<SearchTicketResDTO> page(
            @RequestParam(required = false) Long trainId,
            @RequestParam(required = false) LocalDate departureDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return tickerService.adminPage(trainId, departureDate, pageNum, pageSize);
    }

    @PostMapping("/list")
    @Operation(summary = "批量查询车票信息")
    public List<ListTicketResDTO> list(@RequestBody List<Long> ticketIds) {
        return tickerService.list(ticketIds);
    }

    @GetMapping("/lines")
    @Operation(summary = "获取全部线路（用于下拉选择）")
    public java.util.List<Line> lines() {
        return tickerService.listLines();
    }

    @GetMapping("/trains")
    @Operation(summary = "获取全部火车（用于下拉选择）")
    public java.util.List<TrainInfo> trains() {
        return tickerService.listTrains();
    }
}
