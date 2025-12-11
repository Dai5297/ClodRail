package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.rs.dto.request.mall.AddPointReqDTO;
import com.rs.mapper.PointMapper;
import com.rs.model.PageResult;
import com.rs.model.dto.res.PointHistoryResDTO;
import com.rs.model.dto.res.PointInfoResDTO;
import com.rs.model.mall.PointBalance;
import com.rs.model.mall.PointDetail;
import com.rs.service.PointService;
import com.rs.util.PageUtil;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public PageResult<PointHistoryResDTO> getHistory(
            Integer page,
            Integer size,
            String type,
            String startDate,
            String endDate
    ) {
        // 获取当前用户ID
        Long userId = UserContext.get();

        // 转换类型： earn -> 1, spend -> 2
        Integer typeCode = null;
        if ("earn".equals(type)) {
            typeCode = 1;
        } else if ("spend".equals(type)) {
            typeCode = 2;
        }

        // 分页查询
        PageUtil.startPage(page, size);
        List<PointDetail> details = pointMapper.queryPointHistory(userId, typeCode, startDate, endDate);

        // 转换为DTO
        List<PointHistoryResDTO> records = details.stream().map(detail -> {
            PointHistoryResDTO dto = new PointHistoryResDTO();
            dto.setId(detail.getId());
            dto.setType(detail.getType() == 1 ? "earn" : "spend");
            dto.setPoints(detail.getPoint());
            dto.setDescription(detail.getComment());
            dto.setCreateTime(detail.getCreateTime());
            return dto;
        }).collect(Collectors.toList());

        return PageUtil.buildPageResultFromSource((Page<?>) details, records);
    }
}
