package com.rs.config;

import com.rs.properties.DashscopeAiProperties;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties(DashscopeAiProperties.class)
public class AgentConfig {

    private StreamingChatModel model;

    private String systemMessage;

    private Boolean useMcp = false;

    private McpTransport mcpTransport;

    private Boolean useRAG = false;

    private ContentRetriever contentRetriever;

    private Object[] tools;
}
