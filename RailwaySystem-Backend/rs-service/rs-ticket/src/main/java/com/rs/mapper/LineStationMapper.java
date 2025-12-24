package com.rs.mapper;

import com.rs.model.dto.response.StationResDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LineStationMapper {

    List<StationResDTO> queryStationsByLine(Long lineId);

    void deleteByLineId(Long lineId);

    void batchInsert(@Param("lineId") Long lineId, @Param("stationIds") List<Long> stationIds);
}
