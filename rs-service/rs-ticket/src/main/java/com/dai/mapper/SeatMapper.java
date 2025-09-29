package com.dai.mapper;

import com.dai.model.domain.SeatInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SeatMapper {
    /**
     * 查询车票座位类型
     */
    List<SeatInfo> querySeatTypes(Long trainId);
}
