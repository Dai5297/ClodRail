package com.rs.service;

import com.rs.model.PageResult;
import com.rs.model.dto.response.StationResDTO;
import com.rs.model.ticket.Station;

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

    /**
     * Admin: Page query
     */
    PageResult<Station> adminPage(String name, Integer pageNum, Integer pageSize);

    /**
     * Admin: Add station
     */
    void add(Station station);

    /**
     * Admin: Update station
     */
    void update(Station station);

    /**
     * Admin: Delete station
     */
    void delete(Long id);
}
