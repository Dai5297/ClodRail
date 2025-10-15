package com.rs.mapper;

import com.rs.model.domain.StationInfo;
import com.rs.model.dto.response.StationResDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {

    List<StationResDTO> getAllStations();

    StationInfo getStationInfo(Long originStationId);

    List<StationResDTO> getHotStation();
}
