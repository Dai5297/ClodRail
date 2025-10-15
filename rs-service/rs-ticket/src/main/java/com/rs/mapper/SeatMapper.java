package com.rs.mapper;

import com.rs.model.domain.SeatInfo;
import com.rs.model.dto.response.AvailableSeatResDTO;
import dto.request.FetchSeatReqDTO;
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
    Long fetchSeat(FetchSeatReqDTO fetchSeatReqDTO);
}
