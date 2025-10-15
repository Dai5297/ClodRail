package com.rs.client;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.rs.exception.MqException;
import com.rs.plugins.DelayMessagePostProcessor;
import com.rs.plugins.RabbitMqListenableFutureCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitClient {

    private final RabbitTemplate rabbitTemplate;

    public void sendMsg(String exchange, String routingKey, Object msg) {
        this.sendMsg(exchange, routingKey, msg, 0, false);
    }

    /**
     * 发送消息
     *
     * @param exchange   交换机
     * @param routingKey 路由键
     * @param msg        消息
     * @param delay      延迟时间
     * @param isFailMsg  是否为失败消息
     */
    @Retryable(retryFor = MqException.class, maxAttempts = 3, backoff = @Backoff(value = 1000L, multiplier = 1.5), recover = "recover")
    public void sendMsg(String exchange, String routingKey, Object msg, Integer delay, boolean isFailMsg) {
        // 验证路由键
        Assert.notNull(routingKey);

        CorrelationData correlationData = new CorrelationData();
        // 创建回调处理函数
        RabbitMqListenableFutureCallback futureCallback = RabbitMqListenableFutureCallback.builder()
                .exchange(exchange)
                .routingKey(routingKey)
                .msg(msg)
                .delay(delay)
                .isFailMsg(isFailMsg)
                .build();
        correlationData.getFuture().thenAccept(futureCallback);

        // 消息封装处理
        msg = JSONUtil.toJsonStr(msg);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        Message message = rabbitTemplate.getMessageConverter().
                toMessage(msg, messageProperties);
        try {
            // 发送消息
            rabbitTemplate.convertAndSend(exchange, routingKey, message, new DelayMessagePostProcessor(delay), correlationData);
        } catch (AmqpException e) {
            MqException mqException = new MqException();
            mqException.setMsg(e.getMessage());
            mqException.setMqId(Long.valueOf(message.getMessageProperties().getMessageId()));
            throw mqException;
        }
    }
}
