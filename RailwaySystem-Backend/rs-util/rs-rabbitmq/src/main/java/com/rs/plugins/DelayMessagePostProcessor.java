package com.rs.plugins;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public class DelayMessagePostProcessor implements MessagePostProcessor {

    public static final Long DEFAULT_DELAY = 1000L;
    private Integer delay;

    public DelayMessagePostProcessor(Integer delay) {
        this.delay = delay;
    }

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        message.getMessageProperties().setDelayLong(delay == null ? DEFAULT_DELAY : delay);
        return message;
    }
}
