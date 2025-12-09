package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.rs.dto.request.mall.AddPointReqDTO;
import com.rs.mapper.PointMapper;
import com.rs.model.dto.res.PointInfoResDTO;
import com.rs.model.mall.PointBalance;
import com.rs.model.mall.PointDetail;
import com.rs.service.PointService;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointMapper pointMapper;

    @Override
    public PointInfoResDTO info() {
        PointBalance pointBalance = pointMapper.getPointBalance(UserContext.get());
        return BeanUtil.copyProperties(pointBalance, PointInfoResDTO.class);
    }

    @Override
    @Transactional
    public void addPoint(AddPointReqDTO pointReqDTO) {
        PointDetail pointDetail = new PointDetail();
        pointDetail.setUserId(pointReqDTO.getUserId());
        pointDetail.setType(1);
        pointDetail.setPoint(pointReqDTO.getPrice().intValue());
        pointDetail.setComment(pointReqDTO.getComment());
        pointMapper.addPointDetail(pointDetail);
        if (pointMapper.getPointBalance(pointReqDTO.getUserId()) == null) {
            pointMapper.addPointBalance(pointDetail);
        }else {
            pointMapper.updatePointBalance(pointDetail);
        }
    }
}
