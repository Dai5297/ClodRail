package com.rs.config;

import com.rs.constant.SystemMessage;
import com.rs.factory.AgentFactory;
import dev.langchain4j.model.chat.StreamingChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RailwayAgentConfig {

    private final StreamingChatModel model;

    private final AgentFactory agentFactory;

    @Bean("railwayAgent")
    public Agent agent() {
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setSystemMessage(SystemMessage.RAILWAY_AGENT_SYSTEM_MESSAGE);
        agentConfig.setModel(model);
        return agentFactory.creatAgent(agentConfig);
    }
}
