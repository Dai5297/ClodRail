package com.rs;

import com.rs.client.RabbitClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMq {

    @Autowired
    private RabbitClient rabbitClient;

    @Test
    public void test() {
        rabbitClient.sendMsg("rs.assistant.msg", "assistant.agent", "hello world");
    }

}
