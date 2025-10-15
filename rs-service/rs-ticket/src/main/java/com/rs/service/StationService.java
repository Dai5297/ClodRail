package com.rs.service;

import com.rs.model.dto.response.StationResDTO;

import java.util.List;

public interface StationService {

    /**
     * 获取所有站点信息
     *
     * @return 站点信息
     */
    List<StationResDTO> stationList();

    /**
     * 获取热门站点信息
     *
     * @return 热门站点信息
     */
    List<StationResDTO> hotStation();
}
