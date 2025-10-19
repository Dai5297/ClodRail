package com.rs.service.impl;

import com.rs.dto.request.OccupySeatReqDTO;
import com.rs.dto.response.FetchSeatResDTO;
import com.rs.mapper.SeatMapper;
import com.rs.model.dto.response.AvailableSeatResDTO;
import com.rs.service.SeatService;
import com.rs.dto.request.FetchSeatReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public FetchSeatResDTO fetchSeat(FetchSeatReqDTO fetchSeatReqDTO) {
        FetchSeatResDTO seat = seatMapper.fetchSeat(fetchSeatReqDTO);
        Integer updateNum = seatMapper.preOccupationSeat(seat.getId(), fetchSeatReqDTO.getOrderId());
        return updateNum > 0 ? seat : null;
    }

    @Override
    @Transactional
    public boolean preOccupySeat(OccupySeatReqDTO occupySeatReqDTOd) {
        Integer updateNum = seatMapper.preOccupationSeat(occupySeatReqDTOd.getSeatId(), occupySeatReqDTOd.getOrderId());
        return updateNum > 0;
    }

    @Override
    public void rollbackOccupySeat(Long orderId) {
        seatMapper.rollbackOccupySeat(orderId);
    }
}
