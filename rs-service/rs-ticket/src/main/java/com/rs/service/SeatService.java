package com.rs.service;

import com.rs.dto.request.ticket.FetchSeatReqDTO;
import com.rs.dto.response.ticket.FetchSeatResDTO;
import com.rs.model.dto.response.AvailableSeatResDTO;
import com.rs.model.ticket.Seat;

import java.util.List;

public interface SeatService {

    /**
     * 获取可用座位
     *
     * @param ticketId 订单ID
     * @param seatType 座位类型
     * @return 可用座位
     */
    List<AvailableSeatResDTO> availableSeats(Long ticketId, Integer seatType);

    /**
     * 分配座位
     * @param fetchSeatReqDTO 获取座位参数
     * @return 座位Id
     */
    FetchSeatResDTO fetchSeat(FetchSeatReqDTO fetchSeatReqDTO);

    /**
     * 回滚预占座位
     * @param orderId 订单ID
     */
    void rollbackOccupySeat(Long orderId);

    /**
     * 检查库存
     * @param ticketId 订单ID
     * @return 是否有库存
     */
    boolean checkStock(Long ticketId, Integer seatType);

    /**
     * 查询座位
     * @param orderId 订单ID
     * @return 座位
     */
    Seat querySeat(String orderId);
}
