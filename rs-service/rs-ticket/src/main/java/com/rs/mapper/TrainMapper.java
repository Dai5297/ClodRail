package com.rs.mapper;

import com.rs.model.domain.TrainInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrainMapper {

    TrainInfo getTrainInfo(Long ticketId);
}
