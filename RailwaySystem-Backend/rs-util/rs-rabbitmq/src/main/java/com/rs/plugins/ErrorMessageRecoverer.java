package com.rs.plugins;

import com.rs.client.RabbitClient;
import com.rs.domain.ErrorRabbitMqMessage;
import com.rs.properties.ErrorProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;

@Slf4j
@Data
@RequiredArgsConstructor
public class ErrorMessageRecoverer implements MessageRecoverer {

    private final RabbitClient rabbitClient;
    private final ErrorProperties rabbitmqProperties;

    @Override
    public void recover(Message message, Throwable throwable) {
        log.error("消息消费异常: {}", throwable.getMessage());
        // 指定routingKey的消息才能进入队列
        if(rabbitmqProperties.getError().getWhiteList().contains(message.getMessageProperties().getReceivedRoutingKey())) {
            ErrorRabbitMqMessage errorRabbitMqMessage = new ErrorRabbitMqMessage();
            errorRabbitMqMessage.setOriginRoutingKey(message.getMessageProperties().getReceivedRoutingKey());
            errorRabbitMqMessage.setOriginExchange(message.getMessageProperties().getReceivedExchange());
            errorRabbitMqMessage.setMessage(new String(message.getBody()));
            rabbitClient.sendMsg(rabbitmqProperties.getError().getExchange(), rabbitmqProperties.getError().getRoutingKey(), errorRabbitMqMessage);
        }
    }
}
