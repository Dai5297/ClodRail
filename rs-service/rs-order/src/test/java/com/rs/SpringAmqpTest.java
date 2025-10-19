package com.rs;

import com.rs.client.RabbitClient;
import com.rs.model.domain.CreateTicketOrderMessage;
import com.rs.util.RedisIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

import static com.rs.constant.RedisKeyConstant.TICKET_USER_TIME;

@Slf4j
@SpringBootTest
public class SpringAmqpTest {

    @Autowired
    private RabbitClient rabbitClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisIdUtil redisIdUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRabbitTemplate() {
        String queueName = "simple.queue";
//        String message = "return-callback test";
        CreateTicketOrderMessage message = new CreateTicketOrderMessage(redisIdUtil.nextId("ticket:order"), null);
//        rabbitTemplate.convertAndSend(queueName, message);
        rabbitClient.sendMsg("test.fanout3", "",  message, 10000, false);
//        System.out.println(LocalDateTime.of(2025, 1, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
//        System.out.println(redisIdUtil.nextId("ticket:order"));
    }

    @Test
    public void testLua() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/CheckAndSetRepeatTimeWithDifferentDay.lua"));
        script.setResultType(Long.class);
        Long result = stringRedisTemplate.execute(
                script,
                List.of(TICKET_USER_TIME + "2025:03"),
                String.valueOf(1),
                String.valueOf(50),
                String.valueOf(70),
                String.valueOf(51),
                ":" + 1000
        );
    }
}
