package com.rs.mapper;

import com.rs.model.domain.StopStationInfo;
import com.rs.model.dto.response.HotTicketResDTO;
import com.rs.model.dto.response.SearchTicketResDTO;
import com.rs.model.dto.response.TicketDetailResDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TicketMapper {

    /**
     * 查询最热门的10条车票
     */
    List<HotTicketResDTO> queryHotTicket();

    /**
     * 搜索车票
     */
    List<SearchTicketResDTO> searchTicket(Long originStationId, Long destinationStationId, LocalDate departureDate);

    /**
     * 获取车票详情
     */
    TicketDetailResDTO queryTicketDetail(Long id);

    /**
     * 查询车票的经停站点
     */
    List<StopStationInfo> queryStopoverStations(Long id);

    /**
     * 获取车票的座位价格
     */
    Double queryTicketPrice(Long ticketId, Integer seatType);
}
