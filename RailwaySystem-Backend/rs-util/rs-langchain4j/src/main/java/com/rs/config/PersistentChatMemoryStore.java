package com.rs.config;

import com.rs.mapper.ChatMemoryMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final ChatMemoryMapper chatMemoryMapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String memory = chatMemoryMapper.getMemory((String) memoryId);
        return ChatMessageDeserializer.messagesFromJson(memory);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String memory = chatMemoryMapper.getMemory(memoryId.toString());
        if (memory != null) {
            chatMemoryMapper.updateMemory(memoryId.toString(), ChatMessageSerializer.messagesToJson(messages));
        } else {
            chatMemoryMapper.saveMemory(memoryId.toString(), ChatMessageSerializer.messagesToJson(messages));
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        chatMemoryMapper.deleteMemory(memoryId.toString());
    }
}
