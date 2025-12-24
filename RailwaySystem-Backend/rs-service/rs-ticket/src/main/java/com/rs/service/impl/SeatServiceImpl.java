package com.rs.service.impl;

import com.rs.dto.request.ticket.FetchSeatReqDTO;
import com.rs.dto.response.ticket.FetchSeatResDTO;
import com.rs.dto.response.ticket.SeatInfoResDTO;
import com.rs.dto.response.ticket.SeatTypeInfoResDTO;
import com.rs.mapper.SeatMapper;
import com.rs.model.dto.response.AvailableSeatResDTO;
import com.rs.model.ticket.Seat;
import com.rs.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        if (seat == null) {
            return null;
        }
        Integer updateNum = seatMapper.preOccupationSeat(seat.getId(), fetchSeatReqDTO.getOrderId());
        return updateNum > 0 ? seat : null;
    }

    @Override
    public void rollbackOccupySeat(Long orderId) {
        seatMapper.rollbackOccupySeat(orderId);
    }

    @Override
    public boolean checkStock(Long ticketId, Integer seatType) {
        Integer stock = seatMapper.queryStock(ticketId, seatType);
        return stock > 0;
    }

    @Override
    public Seat querySeat(String orderId) {
        return seatMapper.querySeatPosition(orderId);
    }

    @Override
    public Seat querySeatById(Long seatId) {
        return seatMapper.querySeatById(seatId);
    }

    @Override
    public List<SeatTypeInfoResDTO> ListSeatQuery(List<String> orderId) {
        List<SeatTypeInfoResDTO> list = new ArrayList<>();
        for (String s : orderId) {
            List<SeatInfoResDTO> seatInfoResDTOS = seatMapper.querySeatTypeInfo(s);
            list.add(new SeatTypeInfoResDTO(s, seatInfoResDTOS));
        }
        return list;
    }
}
