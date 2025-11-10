package com.rs.service.impl;

import com.rs.mapper.SeatMapper;
import com.rs.mapper.StationMapper;
import com.rs.mapper.TicketMapper;
import com.rs.mapper.TrainMapper;
import com.rs.model.PageResult;
import com.rs.model.dto.response.HotTicketResDTO;
import com.rs.model.dto.response.SearchTicketResDTO;
import com.rs.model.dto.response.TicketDetailResDTO;
import com.rs.service.TickerService;
import com.rs.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TickerService {

    private final TicketMapper ticketMapper;

    private final SeatMapper seatMapper;

    private final StationMapper stationMapper;

    private final TrainMapper trainMapper;

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
            LocalDate departureDate, Integer pageNum, Integer pageSize
    ) {
        PageUtil.startPage(pageNum, pageSize);
        List<SearchTicketResDTO> searchTicketResDTOS = ticketMapper.searchTicket(originStationId, destinationStationId, departureDate);
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

}
