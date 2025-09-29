package com.dai.service.impl;

import com.dai.mapper.SeatMapper;
import com.dai.mapper.StationMapper;
import com.dai.mapper.TicketMapper;
import com.dai.model.PageResult;
import com.dai.model.dto.request.SearchTicketReqDTO;
import com.dai.model.dto.response.HotTicketResDTO;
import com.dai.model.dto.response.SearchTicketResDTO;
import com.dai.service.TickerService;
import com.dai.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TickerService {

    private final TicketMapper ticketMapper;

    private final SeatMapper seatMapper;

    private final StationMapper stationMapper;

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
     * 搜索车票信息
     *
     * @param reqDTO 搜索车票信息请求参数
     * @return 搜索车票信息
     */
    @Override
    public PageResult<SearchTicketResDTO> searchTicket(SearchTicketReqDTO reqDTO) {
        PageUtil.startPage(reqDTO.getPageNum(), reqDTO.getPageSize());
        List<SearchTicketResDTO> searchTicketResDTOS = ticketMapper.searchTicket(reqDTO);
        searchTicketResDTOS.forEach(searchTicketResDTO -> {
            searchTicketResDTO.setOriginStation(stationMapper.getStationinfo(reqDTO.getOriginStationId()));
            searchTicketResDTO.setSeatTypes(seatMapper.querySeatTypes(searchTicketResDTO.getTrainId()));
            searchTicketResDTO.setDestinationStation(stationMapper.getStationinfo(reqDTO.getDestinationStationId()));
        });
        return PageUtil.buildPageResult(searchTicketResDTOS);
    }
}
