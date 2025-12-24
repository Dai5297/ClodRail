package com.rs.service;

import com.rs.dto.request.mall.AddPointReqDTO;
import com.rs.model.PageResult;
import com.rs.model.dto.res.PointHistoryResDTO;
import com.rs.model.dto.res.PointInfoResDTO;

public interface PointService {

    /**
     * 积分信息
     *
     * @return 积分信息
     */
    PointInfoResDTO info();

    /**
     * 添加积分
     *
     * @param pointReqDTO 添加积分信息
     */
    void addPoint(AddPointReqDTO pointReqDTO);

    /**
     * 查询积分明细
     *
     * @param page      页码
     * @param size      每页数量
     * @param type      类型（earn-获得, spend-消费）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 积分明细列表
     */
    PageResult<PointHistoryResDTO> getHistory(
            Integer page,
            Integer size,
            String type,
            String startDate,
            String endDate
    );
}
