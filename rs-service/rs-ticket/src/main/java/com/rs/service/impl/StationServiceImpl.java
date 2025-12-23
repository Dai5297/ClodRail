package com.rs.service.impl;

import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.StationMapper;
import com.rs.model.PageResult;
import com.rs.model.dto.response.StationResDTO;
import com.rs.model.ticket.Station;
import com.rs.service.StationService;
import com.rs.util.PageUtil;
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

    @Override
    public PageResult<Station> adminPage(String name, Integer pageNum, Integer pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<Station> stations = stationMapper.adminPage(name);
        return PageUtil.buildPageResult(stations);
    }

    @Override
    public void add(Station station) {
        stationMapper.insert(station);
    }

    @Override
    public void update(Station station) {
        stationMapper.update(station);
    }

    @Override
    public void delete(Long id) {
        // Check usage
        int count = stationMapper.countTicketUsage(id);
        if (count > 0) {
            throw new CommonException(RespCode.ERROR, "该车站已被车票使用，无法删除");
        }
        stationMapper.deleteById(id);
    }
}
