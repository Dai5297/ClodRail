package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "rs.alipay")
public class AliPayProperties {

    /**
     * 支付宝分配给开发者的应用ID
     */
    private String appId;

    /**
     * 商户私钥（PKCS8格式）
     */
    private String merchantPrivateKey;

    /**
     * 支付宝公钥（用于验签）
     */
    private String alipayPublicKey;

    /**
     * 异步通知地址（必须公网可访问）
     */
    private String notifyUrl;

    /**
     * 同步跳转地址（支付完成后用户浏览器跳转）
     */
    private String returnUrl;

    /**
     * 签名类型，推荐 RSA2
     */
    private String signType = "RSA2";

    /**
     * 字符编码，推荐 UTF-8
     */
    private String charset = "UTF-8";

    /**
     * 支付宝网关地址（沙箱环境或生产环境）
     */
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
}
