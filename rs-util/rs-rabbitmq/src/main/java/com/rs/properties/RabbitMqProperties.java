package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 共享配置属性类
 * 对应 rs.rabbitmq 前缀的配置，用于 Nacos 共享配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "rs.rabbitmq")
public class RabbitMqProperties {

    /**
     * RabbitMQ 服务器主机地址
     */
    private String host = "localhost";

    /**
     * RabbitMQ 服务器端口号
     */
    private Integer port = 5672;

    /**
     * 连接 RabbitMQ 的用户名
     */
    private String username;

    /**
     * 连接 RabbitMQ 的密码
     */
    private String password;

    /**
     * RabbitMQ 虚拟主机路径
     */
    private String virtualHost = "/test";

    /**
     * RabbitMQ 连接超时时间（毫秒）
     */
    private Integer connectionTimeout = 1000;

    /**
     * 是否启用生产者消息强制路由
     */
    private Boolean templateMandatory = true;

    /**
     * 是否启用生产者消息发送重试机制
     */
    private Boolean templateRetryEnable = false;

    /**
     * 生产者消息确认机制类型
     */
    private String publisherConfirmType = "correlated";

    /**
     * 是否启用生产者消息路由失败回执机制
     */
    private Boolean publisherReturns = true;

    /**
     * 消费者消息确认模式
     */
    private String acknowledgeMode = "none";

    /**
     * 是否启用消费者消息处理失败重试机制
     */
    private Boolean enableListenerRetry = false;

    /**
     * 消费者每次从队列中预取的消息数量
     */
    private Integer prefetch = 1;

    /**
     * 生产者确认机制类型枚举
     */
    public enum ConfirmType {
        NONE,
        SIMPLE,
        CORRELATED
    }

    /**
     * 消费者确认模式枚举
     */
    public enum AckMode {
        NONE,
        MANUAL,
        AUTO
    }

    /**
     * 获取确认机制类型（转换方法）
     */
    public ConfirmType getPublisherConfirmTypeEnum() {
        try {
            return ConfirmType.valueOf(publisherConfirmType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ConfirmType.CORRELATED;
        }
    }

    /**
     * 获取确认模式枚举（转换方法）
     */
    public AckMode getAcknowledgeModeEnum() {
        try {
            return AckMode.valueOf(acknowledgeMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AckMode.NONE;
        }
    }
}