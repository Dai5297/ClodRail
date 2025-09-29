package com.dai.service;

import com.dai.model.dto.response.StationResDTO;

import java.util.List;

public interface StationService {

    /**
     * 获取所有站点信息
     *
     * @return 站点信息
     */
    List<StationResDTO> stationList();
}
