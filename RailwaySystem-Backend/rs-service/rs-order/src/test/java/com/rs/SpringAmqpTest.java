package com.rs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class SpringAmqpTest {

//    @Autowired
//    private RabbitClient rabbitClient;
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    private RedisIdUtil redisIdUtil;
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRabbitTemplate() {
        String queueName = "simple.queue";
//        String message = "return-callback test";
//        CreateTicketOrderMessage message = new CreateTicketOrderMessage(redisIdUtil.nextId("ticket:order"), null);
//        rabbitTemplate.convertAndSend(queueName, message);
//        rabbitClient.sendMsg("test.fanout3", "",  message, 10000, false);
//        System.out.println(LocalDateTime.of(2025, 1, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
//        System.out.println(redisIdUtil.nextId("ticket:order"));
    }

//    @Test
//    public void testLua() {
//        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
//        script.setLocation(new ClassPathResource("lua/CheckAndSetRepeatTimeWithDifferentDay.lua"));
//        script.setResultType(Long.class);
//        Long result = stringRedisTemplate.execute(
//                script,
//                List.of(TICKET_USER_TIME + "2025:03"),
//                String.valueOf(1),
//                String.valueOf(50),
//                String.valueOf(70),
//                String.valueOf(51),
//                ":" + 1000
//        );
//    }
}
