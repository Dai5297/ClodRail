package com.dai.service.impl;

import com.dai.mapper.StationMapper;
import com.dai.model.dto.response.StationResDTO;
import com.dai.service.StationService;
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
}
