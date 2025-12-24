package com.rs.config;

import com.rs.client.RabbitClient;
import com.rs.plugins.ErrorMessageRecoverer;
import com.rs.properties.ErrorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.rabbitmq.listener.simple.retry", name= "enabled", havingValue = "true")
public class MessageRecovererConfig {

    private final RabbitClient rabbitClient;

    private final ErrorProperties rabbitmqProperties;

    @Bean
    public Queue errorQueue() {
        return new Queue(rabbitmqProperties.getError().getQueue());
    }

    @Bean
    public Exchange errorExchange() {
        return new TopicExchange(rabbitmqProperties.getError().getExchange(), true, false);
    }

    @Bean
    public Binding errorBinding() {
        return BindingBuilder.bind(errorQueue())
                .to(errorExchange())
                .with(rabbitmqProperties.getError().getRoutingKey())
                .noargs();
    }

    @Bean
    @ConditionalOnMissingBean(MessageRecoverer.class)
    public MessageRecoverer messageRecoverer() {
        return new ErrorMessageRecoverer(rabbitClient, rabbitmqProperties);
    }
}
