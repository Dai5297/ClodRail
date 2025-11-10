package com.rs;

import com.rs.config.Agent;
import com.rs.config.AgentConfig;
import com.rs.factory.AgentFactory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestAgent {

    @Autowired
    private AgentFactory agentFactory;

    @Autowired
    private StreamingChatModel model;

    @Autowired
    private ChatModel chatModel;

    @Test
    public void test() {
        AgentConfig config = new AgentConfig();
        config.setModel(model);
        config.setSystemMessage("你是一个铁路系统的智能客服");
        Agent agent = agentFactory.creatAgent(config);
        Flux<String> chat = agent.chat("1", "我是谁");
        chat.doOnNext(System.out::print)
                .doOnComplete(System.out::println)
                .doOnError(throwable -> {
                    System.out.println(throwable.getMessage());
                }).subscribe();
        while (true) {

        }
    }

}
