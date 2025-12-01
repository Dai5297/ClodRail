package com.rs.mapper;

import com.rs.model.ticket.Line;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LineMapper {

    Line selectById(Long id);
}
