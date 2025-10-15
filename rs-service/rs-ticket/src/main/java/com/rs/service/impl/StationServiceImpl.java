package com.rs.service.impl;

import com.rs.mapper.StationMapper;
import com.rs.model.dto.response.StationResDTO;
import com.rs.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final StationMapper stationMapper;

    /**
     * 获取所有站点信息
     *
     * @return 站点信息
     */
    @Override
    public List<StationResDTO> stationList() {
        return stationMapper.getAllStations();
    }

    /**
     * 获取热门站点信息
     *
     * @return 热门站点信息
     */
    @Override
    public List<StationResDTO> hotStation() {
        return stationMapper.getHotStation();
    }
}
