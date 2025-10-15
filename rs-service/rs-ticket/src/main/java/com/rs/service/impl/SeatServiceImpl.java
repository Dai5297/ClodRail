package com.rs.service.impl;

import com.rs.mapper.SeatMapper;
import com.rs.model.dto.response.AvailableSeatResDTO;
import com.rs.service.SeatService;
import dto.request.FetchSeatReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatMapper seatMapper;

    /**
     * 获取可用座位
     * @param ticketId 订单ID
     * @param seatType 座位类型
     * @return 可用座位
     */

    @Override
    public List<AvailableSeatResDTO> availableSeats(Long ticketId, Integer seatType) {
        return seatMapper.queryAvailableSeats(ticketId, seatType);
    }

    @Override
    public Long fetchSeat(FetchSeatReqDTO fetchSeatReqDTO) {
        return seatMapper.fetchSeat(fetchSeatReqDTO);
    }
}
