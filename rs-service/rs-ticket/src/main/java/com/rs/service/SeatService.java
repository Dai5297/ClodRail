package com.rs.service;

import com.rs.dto.request.OccupySeatReqDTO;
import com.rs.dto.response.FetchSeatResDTO;
import com.rs.model.dto.response.AvailableSeatResDTO;
import com.rs.dto.request.FetchSeatReqDTO;

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
     * 预占座位
     * @param occupySeatReqDTO 预占座位参数
     * @return 是否成功
     */
    boolean preOccupySeat(OccupySeatReqDTO occupySeatReqDTO);

    /**
     * 回滚预占座位
     * @param orderId 订单ID
     */
    void rollbackOccupySeat(Long orderId);
}
