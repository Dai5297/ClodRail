package com.rs.service;

import com.rs.model.order.Order;

public interface CommonService {

    /**
     * 生成订单短链接
     *
     * @return 链接
     */
    String generateAssistantOrder(Order order);

    /**
     * 解析订单短链接
     *
     * @param uuid 链接
     * @return 订单信息
     */
    Order praseAssistantOrder(String uuid);
}
