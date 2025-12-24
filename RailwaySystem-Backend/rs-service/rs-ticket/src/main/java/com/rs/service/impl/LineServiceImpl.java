package com.rs.service.impl;

import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.LineMapper;
import com.rs.mapper.LineStationMapper;
import com.rs.model.PageResult;
import com.rs.model.dto.request.LineFormDTO;
import com.rs.model.dto.response.StationResDTO;
import com.rs.model.ticket.Line;
import com.rs.service.LineService;
import com.rs.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {

    private final LineMapper lineMapper;
    private final LineStationMapper lineStationMapper;

    @Override
    public List<Line> listAll() {
        return lineMapper.listAll();
    }

    @Override
    public PageResult<Line> adminPage(String name, Integer pageNum, Integer pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<Line> lines = lineMapper.adminPage(name);
        return PageUtil.buildPageResult(lines);
    }

    @Override
    public void add(LineFormDTO dto) {
        Line line = new Line();
        line.setId(dto.getId());
        line.setName(dto.getName());
        line.setCode(dto.getCode());
        line.setTotalLength(dto.getTotalLength());
        line.setDesignedSpeed(dto.getDesignedSpeed());
        line.setStatus(dto.getStatus());
        line.setStartStation(dto.getStartStation());
        line.setEndStation(dto.getEndStation());
        lineMapper.insert(line);
        if (dto.getStopStations() != null && !dto.getStopStations().isEmpty()) {
            lineStationMapper.batchInsert(line.getId(), dto.getStopStations());
        }
    }

    @Override
    public void update(LineFormDTO dto) {
        Line line = new Line();
        line.setId(dto.getId());
        line.setName(dto.getName());
        line.setCode(dto.getCode());
        line.setTotalLength(dto.getTotalLength());
        line.setDesignedSpeed(dto.getDesignedSpeed());
        line.setStatus(dto.getStatus());
        line.setStartStation(dto.getStartStation());
        line.setEndStation(dto.getEndStation());
        lineMapper.update(line);
        lineStationMapper.deleteByLineId(line.getId());
        if (dto.getStopStations() != null && !dto.getStopStations().isEmpty()) {
            lineStationMapper.batchInsert(line.getId(), dto.getStopStations());
        }
    }

    @Override
    public void delete(Long id) {
        // Check usage in tickets
        int count = lineMapper.countTicketUsage(id);
        if (count > 0) {
            throw new CommonException(RespCode.ERROR, "该线路已被车票使用，无法删除");
        }
        lineMapper.deleteById(id);
    }

    @Override
    public List<StationResDTO> listStations(Long lineId) {
        return lineStationMapper.queryStationsByLine(lineId);
    }
}
