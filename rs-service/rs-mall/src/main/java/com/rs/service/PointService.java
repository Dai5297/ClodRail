package com.rs.service;

import com.rs.dto.request.mall.AddPointReqDTO;
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
}
