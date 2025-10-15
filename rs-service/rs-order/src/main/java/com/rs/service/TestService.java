package com.rs.service;

import com.rs.model.dto.request.OrderCreateReqDTO;
import com.rs.model.dto.response.OrderCreateResDTO;

public interface TestService {

    OrderCreateResDTO bookTicket(OrderCreateReqDTO reqDTO);
}
