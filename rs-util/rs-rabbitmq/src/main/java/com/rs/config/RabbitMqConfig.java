package com.rs.config;

import com.rs.properties.RabbitMqProperties;
import com.rs.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.nio.charset.Charset;

@Slf4j
@EnableRetry
@Configuration
@EnableConfigurationProperties({RabbitMqProperties.class})
public class RabbitMqConfig {

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setCreateMessageIds(false);
        return jackson2JsonMessageConverter;
    }

    @Bean
    public MessagePostProcessor messagePostProcessor() {
        return message -> {
            message.getMessageProperties().setMessageId(String.valueOf(SnowflakeUtil.nextId()));
            return message;
        };
    }

    /**
     * 通过BeanPostProcessor配置RabbitTemplate 添加消息序列化器及消息ID生成器 并配置生产者路由失败的ReturnCallback
     *
     * @return 处理过RabbitTemplate的BeanPostProcessor
     */
    @Bean
    public static BeanPostProcessor rabbitTemplatePostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof RabbitTemplate) {
                    RabbitTemplate rabbitTemplate = (RabbitTemplate) bean;

                    // 获取配置类实例（通过静态方法避免依赖注入）
                    RabbitMqConfig config = new RabbitMqConfig();

                    // 配置消息转换器及消息ID生成器
                    rabbitTemplate.setMessageConverter(config.jsonMessageConverter());
                    rabbitTemplate.addBeforePublishPostProcessors(config.messagePostProcessor());

                    // 配置ReturnCallback
                    rabbitTemplate.setMandatory(true);
                    rabbitTemplate.setReturnsCallback(returnedMessage -> {
                        byte[] body = returnedMessage.getMessage().getBody();
                        String messageId = returnedMessage.getMessage().getMessageProperties().getMessageId();
                        String content = new String(body, Charset.defaultCharset());
                        log.info("消息发送失败，应答码{}，原因{}，交换机{}，路由键{},消息id{},消息内容{}",
                                returnedMessage.getReplyCode(),
                                returnedMessage.getReplyText(),
                                returnedMessage.getExchange(),
                                returnedMessage.getRoutingKey(),
                                messageId,
                                content);
                    });
                }
                return bean;
            }
        };
    }

    /**
     * 创建Canal监听容器工厂
     *
     * @param connectionFactory 连接工厂
     * @return 监听容器工厂
     */
    @Bean("canalListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory canalListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new SimpleMessageConverter()); // 只处理 String/byte[]
        return factory;
    }
}