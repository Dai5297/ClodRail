package com.rs.plugins;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.util.function.Consumer;

@Slf4j
@Builder
public class RabbitMqListenableFutureCallback implements Consumer<CorrelationData.Confirm> {

    private String exchange;

    private String routingKey;

    private Object msg;

    private Integer delay;

    private boolean isFailMsg;

    @Override
    public void accept(CorrelationData.Confirm confirm) {
        if (confirm.isAck()) {
            // 成功
            log.info("消息投递到交换机成功: {}", confirm);
        } else {
            // 失败
            log.error("消息投递到交换机失败: {}", confirm);
        }
    }
}
