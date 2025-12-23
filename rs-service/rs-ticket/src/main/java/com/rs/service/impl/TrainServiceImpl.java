package com.rs.service.impl;

import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.TrainMapper;
import com.rs.model.PageResult;
import com.rs.model.ticket.Train;
import com.rs.service.TrainService;
import com.rs.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private final TrainMapper trainMapper;

    @Override
    public PageResult<Train> adminPage(String name, String type, Integer status, Integer pageNum, Integer pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<Train> trains = trainMapper.adminPage(name, type, status);
        return PageUtil.buildPageResult(trains);
    }

    @Override
    public void add(Train train) {
        trainMapper.insert(train);
    }

    @Override
    public void update(Train train) {
        trainMapper.update(train);
    }

    @Override
    public void delete(Long id) {
        int cnt = trainMapper.countTicketUsage(id);
        if (cnt > 0) {
            throw new CommonException(RespCode.ERROR, "该列车已被车票使用，无法删除");
        }
        trainMapper.deleteById(id);
    }
}
