package com.rs.service;

import com.rs.model.dto.response.AvailableSeatResDTO;
import dto.request.FetchSeatReqDTO;

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
    Long fetchSeat(FetchSeatReqDTO fetchSeatReqDTO);
}
