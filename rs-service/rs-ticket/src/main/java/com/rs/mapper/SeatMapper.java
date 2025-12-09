package com.rs.mapper;

import com.rs.dto.request.ticket.FetchSeatReqDTO;
import com.rs.dto.response.ticket.FetchSeatResDTO;
import com.rs.dto.response.ticket.SeatInfoResDTO;
import com.rs.model.domain.SeatInfo;
import com.rs.model.dto.response.AvailableSeatResDTO;
import com.rs.model.ticket.Seat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SeatMapper {
    /**
     * 查询车票座位类型
     */
    List<SeatInfo> querySeatTypes(Long id);

    /**
     * 查询车票可用座位数
     */
    List<AvailableSeatResDTO> queryAvailableSeats(Long ticketId, Integer seatType);

    /**
     * 获取座位位置
     */
    FetchSeatResDTO fetchSeat(FetchSeatReqDTO fetchSeatReqDTO);

    /**
     * 座位回滚
     */
    void rollbackOccupySeat(Long orderId);

    /**
     * 预占座位
     */
    Integer preOccupationSeat(Long seatId, String orderId);

    /**
     * 获取座位库存
     */
    Integer queryStock(Long ticketId, Integer seatType);

    /**
     * 获取座位位置
     */
    Seat querySeatPosition(String orderId);

    List<SeatInfoResDTO> querySeatTypeInfo(String orderId);
}
