package com.rs.service.impl;

import com.rs.model.dto.request.OrderCreateReqDTO;
import com.rs.model.dto.response.OrderCreateResDTO;
import com.rs.service.OrderService;
import com.rs.service.TestService;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final OrderService orderService;

    @Override
    @GlobalTransactional
    public OrderCreateResDTO bookTicket(OrderCreateReqDTO reqDTO) {
        return orderService.createOrder(reqDTO); // 这会触发 TCC Try，并注册资源
    }
}
