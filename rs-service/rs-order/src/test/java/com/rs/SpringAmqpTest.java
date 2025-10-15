package com.rs;

import com.rs.client.RabbitClient;
import com.rs.util.RedisIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class SpringAmqpTest {

    @Autowired
    private RabbitClient rabbitClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisIdUtil redisIdUtil;

    @Test
    public void testRabbitTemplate() {
        String queueName = "simple.queue";
        String message = "return-callback test";
//        rabbitTemplate.convertAndSend(queueName, message);
//        rabbitClient.sendMsg("test.fanout3", "",  message, 10000, false);
//        System.out.println(LocalDateTime.of(2025, 1, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
        System.out.println(redisIdUtil.nextId("ticket:order"));
    }


}
