package com.rs.mapper;

import com.rs.model.mall.PointBalance;
import com.rs.model.mall.PointDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointMapper {

    PointBalance getPointBalance(Long userId);

    void addPointDetail(PointDetail pointDetail);

    void addPointBalance(PointDetail pointDetail);

    void updatePointBalance(PointDetail pointDetail);
}
