package com.rs.config;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.rs.properties.AliPayProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AliPayProperties.class)
public class AliPayConfig {

    private final AliPayProperties aliPayProperties;

    @PostConstruct
    public void initAliPay() {
        log.info("初始化支付宝配置");
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipaydev.com";
        config.appId = aliPayProperties.getAppId();
        config.merchantPrivateKey = aliPayProperties.getMerchantPrivateKey();
        config.alipayPublicKey = aliPayProperties.getAlipayPublicKey();
        config.signType = aliPayProperties.getSignType();
        config.notifyUrl = aliPayProperties.getNotifyUrl();
        config.protocol = "https";
        config.ignoreSSL = true;
        Factory.setOptions(config);
        log.info("支付宝配置初始化成功");
    }
}
