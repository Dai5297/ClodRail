package com.rs.service;

import com.rs.dto.request.ticket.AssistantOrderMsgDTO;
import com.rs.dto.response.ticket.ListTicketResDTO;
import com.rs.model.PageResult;
import com.rs.model.dto.response.HotTicketResDTO;
import com.rs.model.dto.response.SearchTicketResDTO;
import com.rs.model.dto.response.TicketDetailResDTO;

import com.rs.model.dto.request.TicketFormDTO;

import com.rs.model.ticket.Ticket;
import com.rs.model.ticket.Line;
import com.rs.model.domain.TrainInfo;

import java.time.LocalDate;
import java.util.List;

public interface TickerService {

    /**
     * 根据ID查询车票实体
     */
    Ticket getTicketById(Long id);

    /**
     * 新增车票
     * 
     * @param dto
     */
    void addTicket(TicketFormDTO dto);

    /**
     * 更新车票
     * 
     * @param dto
     */
    void updateTicket(TicketFormDTO dto);

    /**
     * 删除车票
     * 
     * @param id
     */
    void deleteTicket(Long id);

    /**
     * 管理端分页查询
     */
    PageResult<SearchTicketResDTO> adminPage(Long trainId, LocalDate departureDate, Integer pageNum, Integer pageSize);

    /**
     * 获取最热门的10条车票信息
     *
     * @return 最热门的10条车票信息
     */
    List<HotTicketResDTO> hotTicket();

    /**
     * 搜索车票
     *
     * @param originStationId      出发站ID
     * @param destinationStationId 目的地站ID
     * @param departureDate        出发时间
     * @param pageNum              页码
     * @param pageSize             每页数量
     * @return 搜索结果
     */
    PageResult<SearchTicketResDTO> searchTicket(
            Long originStationId, Long destinationStationId,
            LocalDate departureDate, Integer pageNum, Integer pageSize);

    /**
     * 获取车票详情
     *
     * @param ticketId 车票ID
     * @return 车票详情
     */
    TicketDetailResDTO ticketDetail(Long ticketId);

    /**
     * 查询车票价格
     *
     * @param ticketId 车票ID
     * @return 车票价格
     */
    Double queryTicketPrice(Long ticketId, Integer seatType);

    /**
     * 批量查询车票信息
     *
     * @param ticketIds 车票ID列表
     * @return 车票信息
     */
    List<ListTicketResDTO> list(List<Long> ticketIds);

    /**
     * 查询助手订单信息
     *
     * @param ticketId 订单ID
     * @return 助手订单信息
     */
    AssistantOrderMsgDTO queryOrderMsgDetail(Long ticketId);

    /**
     * 管理端：获取全部线路
     */
    List<Line> listLines();

    /**
     * 管理端：获取全部火车
     */
    List<TrainInfo> listTrains();
}
