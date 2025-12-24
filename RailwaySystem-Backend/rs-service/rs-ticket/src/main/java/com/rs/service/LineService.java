package com.rs.service;

import com.rs.model.PageResult;
import com.rs.model.dto.request.LineFormDTO;
import com.rs.model.dto.response.StationResDTO;
import com.rs.model.ticket.Line;

import java.util.List;

public interface LineService {

    List<Line> listAll();

    /**
     * Admin: Page query
     */
    PageResult<Line> adminPage(String name, Integer pageNum, Integer pageSize);

    /**
     * Admin: Add line
     */
    void add(LineFormDTO line);

    /**
     * Admin: Update line
     */
    void update(LineFormDTO line);

    /**
     * Admin: Delete line
     */
    void delete(Long id);

    /**
     * Admin: List stations passing through the line (ordered)
     */
    List<StationResDTO> listStations(Long lineId);
}
