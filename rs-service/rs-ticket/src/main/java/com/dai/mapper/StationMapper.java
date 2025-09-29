package com.dai.mapper;

import com.dai.model.domain.StationInfo;
import com.dai.model.dto.response.StationResDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {

    List<StationResDTO> getAllStations();

    StationInfo getStationinfo(Long originStationId);
}
