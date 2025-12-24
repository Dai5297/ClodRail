package com.rs.mapper;

import com.rs.model.mall.PointBalance;
import com.rs.model.mall.PointDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PointMapper {

    PointBalance getPointBalance(Long userId);

    void addPointDetail(PointDetail pointDetail);

    void addPointBalance(PointDetail pointDetail);

    void updatePointBalance(PointDetail pointDetail);

    /**
     * 扣减积分（消费）
     *
     * @param userId 用户ID
     * @param points 扣减积分数
     * @return 影响行数
     */
    int deductPoints(Long userId, Integer points);

    /**
     * 添加积分消费明细
     *
     * @param pointDetail 积分明细
     */
    void addSpendDetail(PointDetail pointDetail);

    /**
     * 分页查询积分明细
     *
     * @param userId    用户ID
     * @param type      类型（1-获得 2-消赹）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 积分明细列表
     */
    List<PointDetail> queryPointHistory(
            @Param("userId") Long userId,
            @Param("type") Integer type,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}
