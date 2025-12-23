package com.rs.mapper;

import com.rs.dto.request.ticket.AssistantOrderMsgDTO;
import com.rs.dto.response.ticket.ListTicketResDTO;
import com.rs.model.domain.StopStationInfo;
import com.rs.model.dto.response.HotTicketResDTO;
import com.rs.model.dto.response.SearchTicketResDTO;
import com.rs.model.dto.response.TicketDetailResDTO;
import com.rs.model.ticket.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TicketMapper {

    /**
     * 查询最热门的10条车票
     */
    List<HotTicketResDTO> queryHotTicket();

    List<Long> queryHotTicketIdsByDate(@Param("departureDate") LocalDate departureDate);

    List<Ticket> queryHotTicketsStartingBetween(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

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

    /**
     * 根据车票ID查询车票信息
     */
    Ticket queryById(Long ticketId);

    /**
     * 查询车票列表
     */
    List<ListTicketResDTO> querylist(@Param("ticketIds") List<Long> ticketIds);

    /**
     * 查询助手订单信息
     */
    AssistantOrderMsgDTO queryOrderMsgDetail(@Param("ticketId") Long ticketId);

    /**
     * 新增车票
     */
    void insert(Ticket ticket);

    /**
     * 更新车票
     */
    void update(Ticket ticket);

    /**
     * 删除车票
     */
    void deleteById(Long id);

    /**
     * 管理端查询车票
     */
    List<SearchTicketResDTO> adminQuery(@Param("trainId") Long trainId, @Param("departureDate") LocalDate departureDate);
}
