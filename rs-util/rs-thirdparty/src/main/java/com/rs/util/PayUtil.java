package com.rs.util;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rs.properties.AliPayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PayUtil {

    private final AliPayProperties aliPayProperties;

    public String createAliPay(String orderId, Double amount, String subject) throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayProperties.getGatewayUrl(),
                aliPayProperties.getAppId(),
                aliPayProperties.getMerchantPrivateKey(),
                "JSON",
                aliPayProperties.getCharset(),
                aliPayProperties.getAlipayPublicKey(),
                aliPayProperties.getSignType()
        );

        // 准备请求参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayProperties.getNotifyUrl());
        request.setReturnUrl(aliPayProperties.getReturnUrl());

        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", String.valueOf(amount));
        bizContent.put("subject", subject);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        String bizContentJson = new ObjectMapper().writeValueAsString(bizContent);
        request.setBizContent(bizContentJson);

        // 发起支付请求 返回html页面
        return alipayClient.pageExecute(request).getBody();
    }

}
