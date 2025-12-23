package com.rs.service.impl;

import com.rs.dto.request.ticket.AssistantOrderMsgDTO;
import com.rs.dto.response.ticket.ListTicketResDTO;
import com.rs.mapper.LineMapper;
import com.rs.mapper.SeatMapper;
import com.rs.mapper.StationMapper;
import com.rs.mapper.TrainMapper;
import com.rs.mapper.TicketMapper;
import com.rs.model.PageResult;
import com.rs.model.dto.response.HotTicketResDTO;
import com.rs.model.dto.response.SearchTicketResDTO;
import com.rs.model.dto.response.TicketDetailResDTO;
import com.rs.service.TickerService;
import com.rs.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.rs.model.dto.request.TicketFormDTO;
import com.rs.model.ticket.Ticket;
import com.rs.model.ticket.Line;
import com.rs.model.domain.TrainInfo;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TickerService {

    private final TicketMapper ticketMapper;

    private final SeatMapper seatMapper;

    private final StationMapper stationMapper;

    private final LineMapper lineMapper;
    private final TrainMapper trainMapper;

    @Override
    public Ticket getTicketById(Long id) {
        return ticketMapper.queryById(id);
    }

    @Override
    public void addTicket(TicketFormDTO dto) {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(dto, ticket);
        ticketMapper.insert(ticket);
    }

    @Override
    public void updateTicket(TicketFormDTO dto) {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(dto, ticket);
        ticketMapper.update(ticket);
    }

    @Override
    public void deleteTicket(Long id) {
        ticketMapper.deleteById(id);
    }

    @Override
    public PageResult<SearchTicketResDTO> adminPage(Long trainId, LocalDate departureDate, Integer pageNum,
            Integer pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<SearchTicketResDTO> list = ticketMapper.adminQuery(trainId, departureDate);
        list.forEach(dto -> {
            dto.setSeatTypes(seatMapper.querySeatTypes(dto.getId()));
        });
        return PageUtil.buildPageResult(list);
    }

    /**
     * 获取热门车票
     *
     * @return 热门车票
     */
    @Override
    public List<HotTicketResDTO> hotTicket() {
        return ticketMapper.queryHotTicket();
    }

    /**
     * 搜索车票
     *
     * @param originStationId      出发站ID
     * @param destinationStationId 目的站ID
     * @param departureDate        出发时间
     * @param pageNum              页码
     * @param pageSize             页大小
     * @return 搜索结果
     */
    @Override
    public PageResult<SearchTicketResDTO> searchTicket(
            Long originStationId, Long destinationStationId,
            LocalDate departureDate, Integer pageNum, Integer pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<SearchTicketResDTO> searchTicketResDTOS = ticketMapper.searchTicket(originStationId, destinationStationId,
                departureDate);
        searchTicketResDTOS.forEach(searchTicketResDTO -> {
            searchTicketResDTO.setOriginStation(stationMapper.getStationInfo(originStationId));
            searchTicketResDTO.setSeatTypes(seatMapper.querySeatTypes(searchTicketResDTO.getId()));
            searchTicketResDTO.setDestinationStation(stationMapper.getStationInfo(destinationStationId));
        });
        return PageUtil.buildPageResult(searchTicketResDTOS);
    }

    @Override
    public TicketDetailResDTO ticketDetail(Long ticketId) {
        TicketDetailResDTO ticketDetailResDTO = ticketMapper.queryTicketDetail(ticketId);
        ticketDetailResDTO.setSeatTypes(seatMapper.querySeatTypes(ticketId));
        ticketDetailResDTO.setStopoverStations(ticketMapper.queryStopoverStations(ticketId));
        return ticketDetailResDTO;
    }

    @Override
    public Double queryTicketPrice(Long ticketId, Integer seatType) {
        return ticketMapper.queryTicketPrice(ticketId, seatType);
    }

    @Override
    public List<ListTicketResDTO> list(List<Long> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return List.of();
        }
        return ticketMapper.querylist(ticketIds);
    }

    @Override
    public AssistantOrderMsgDTO queryOrderMsgDetail(Long ticketId) {
        return ticketMapper.queryOrderMsgDetail(ticketId);
    }

    @Override
    public List<Line> listLines() {
        return lineMapper.listAll();
    }

    @Override
    public List<TrainInfo> listTrains() {
        return trainMapper.listAll();
    }

}
