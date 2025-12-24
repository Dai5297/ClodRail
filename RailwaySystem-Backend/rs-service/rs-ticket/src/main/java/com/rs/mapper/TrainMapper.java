package com.rs.mapper;

import com.rs.model.domain.TrainInfo;
import com.rs.model.ticket.Train;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrainMapper {

    TrainInfo getTrainInfo(Long ticketId);

    java.util.List<TrainInfo> listAll();

    java.util.List<Train> adminPage(@Param("name") String name,
            @Param("type") String type,
            @Param("status") Integer status);

    void insert(Train train);

    void update(Train train);

    void deleteById(Long id);

    int countTicketUsage(Long trainId);
}
