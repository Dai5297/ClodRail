package com.rs.service.impl;

import com.alipay.easysdk.factory.Factory;
import com.rs.client.RabbitClient;
import com.rs.mapper.OrderMapper;
import com.rs.model.order.Order;
import com.rs.service.PayService;
import com.rs.util.PayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final PayUtil payUtil;

    private final OrderMapper orderMapper;

    private final RabbitClient rabbitClient;

    /**
     * 支付订单
     *
     * @param orderId 订单id
     * @return 订单支付结果
     */
    @Override
    public String payOrder(String orderId) {
        String result;
        Order order = orderMapper.queryByOrderId(orderId);
        try {
            result = payUtil.createAliPay(orderId, order.getAmount(), "支付订单" + orderId);
        } catch (Exception e) {
            log.info("创建支付宝订单失败");
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public String payNotify(HttpServletRequest request) throws Exception {
        log.info("支付宝支付回调");
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }
        String orderId = params.get("out_trade_no");
        if (Factory.Payment.Common().verifyNotify(params)) {
            orderMapper.updateAliPayStatus(orderId);
        }
        rabbitClient.sendMsg("rs.ticket.order", "order.point", orderId);
        return "success";
    }
}
