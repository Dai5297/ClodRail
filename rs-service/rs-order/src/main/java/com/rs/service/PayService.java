package com.rs.service;

import jakarta.servlet.http.HttpServletRequest;

public interface PayService {

    /**
     * 支付订单
     * @param orderId 订单id
     * @return 支付结果
     */
    String payOrder(String orderId);

    /**
     * 支付回调
     * @param request 请求
     * @return 回调结果
     */
    String payNotify(HttpServletRequest request) throws Exception;
}
