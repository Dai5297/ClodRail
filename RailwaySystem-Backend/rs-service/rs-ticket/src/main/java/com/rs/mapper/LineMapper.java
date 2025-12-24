package com.rs.mapper;

import com.rs.model.ticket.Line;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LineMapper {

    Line selectById(Long id);

    List<Line> listAll();

    List<Line> adminPage(@Param("name") String name);

    void insert(Line line);

    void update(Line line);

    void deleteById(Long id);

    int countTicketUsage(Long lineId);
}
