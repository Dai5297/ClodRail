package com.rs.factory;

import com.rs.config.Agent;
import com.rs.config.AgentConfig;
import com.rs.config.PersistentChatMemoryStore;
import com.rs.exception.AgentException;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.logging.DefaultMcpLogMessageHandler;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentFactory {

    private final PersistentChatMemoryStore persistentChatMemoryStore;

    public Agent creatAgent(AgentConfig config) {
        if (config.getUseMcp() && config.getMcpTransport() == null) {
            throw new AgentException("McpTransport is null");
        }

        if (config.getUseRAG() && config.getContentRetriever() == null) {
            throw new AgentException("ContentRetriever is null");
        }

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory
                .builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(persistentChatMemoryStore)
                .build();

        AiServices<Agent> agentAiServices = AiServices.builder(Agent.class)
                .systemMessageProvider(ignor -> config.getSystemMessage())
                .streamingChatModel(config.getModel())
                .chatMemoryProvider(chatMemoryProvider);

        // 启用RAG功能
        if (config.getUseRAG()) {
            agentAiServices.contentRetriever(config.getContentRetriever());
        }

        // 启用MCP功能
        if (config.getUseMcp()) {
            McpClient client = new DefaultMcpClient.Builder()
                    .transport(config.getMcpTransport())
                    .logHandler(new DefaultMcpLogMessageHandler())
                    .build();

            ToolProvider toolProvider = McpToolProvider.builder()
                    .mcpClients(client)
                    .build();
            agentAiServices.toolProvider(toolProvider);
        }

        // 启用Function Calling功能
        if (config.getTools() != null && config.getTools().length > 0) {
            agentAiServices.tools(config.getTools());
        }

        return agentAiServices
                .build();
    }
}
