package com.rs.mapper;

import com.rs.dto.response.FetchSeatResDTO;
import com.rs.model.domain.SeatInfo;
import com.rs.model.dto.response.AvailableSeatResDTO;
import com.rs.dto.request.FetchSeatReqDTO;
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
     * 预占座位
     */
    Integer preOccupationSeat(Long seatId, Long orderId);

    /**
     * 座位回滚
     */
    void rollbackOccupySeat(Long orderId);
}
