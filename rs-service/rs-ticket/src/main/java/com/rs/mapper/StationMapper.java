package com.rs.mapper;

import com.rs.model.domain.StationInfo;
import com.rs.model.dto.response.StationResDTO;
import com.rs.model.ticket.Station;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StationMapper {

    List<StationResDTO> getAllStations();

    StationInfo getStationInfo(Long originStationId);

    List<StationResDTO> getHotStation();

    List<Station> adminPage(@Param("name") String name);

    void insert(Station station);

    void update(Station station);

    void deleteById(Long id);

    /**
     * Check if the station is used in any ticket (via Line or StopStations)
     */
    int countTicketUsage(Long stationId);
}
