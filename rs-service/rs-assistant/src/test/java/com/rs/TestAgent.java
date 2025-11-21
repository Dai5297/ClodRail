package com.rs;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import com.rs.config.Agent;
import com.rs.config.AgentConfig;
import com.rs.factory.AgentFactory;
import com.rs.model.entity.Message;
import com.rs.util.ZSetUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest
public class TestAgent {

    @Autowired
    private AgentFactory agentFactory;

    @Autowired
    private StreamingChatModel model;

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ZSetUtil zSetUtil;

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

    @Test
    public void testURI() throws URISyntaxException {
        String uriStr = "ws://localhost:18080/ws/assistant?sessionId=123&token=abc";
        URI uri = new URI(uriStr);
        UrlQuery urlQuery = UrlQuery.of(uri.getQuery(), CharsetUtil.CHARSET_UTF_8);
        String sessionId = (String) urlQuery.get("sessionId");
        System.out.println(sessionId);
        System.out.println(urlQuery.get("token"));
    }

    @Test
    public void testLoad() {
        String messageText = "{\"type\":\"user_message\",\"userId\":\"1\",\"sessionId\":\"1\",\"content\":\"1111\"}";
        JSONObject jsonObject = new JSONObject(messageText);
        System.out.println(jsonObject.toBean(Message.class));
    }

}
