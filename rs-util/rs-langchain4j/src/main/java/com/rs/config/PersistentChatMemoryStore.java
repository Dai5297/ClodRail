package com.rs.config;

import com.rs.mapper.MemoryMapper;
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

    private final MemoryMapper memoryMapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String memory = memoryMapper.getMemory((String) memoryId);
        return ChatMessageDeserializer.messagesFromJson(memory);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String memory = memoryMapper.getMemory(memoryId.toString());
        if (memory != null) {
            memoryMapper.updateMemory(memoryId.toString(), ChatMessageSerializer.messagesToJson(messages));
        } else {
            memoryMapper.saveMemory(memoryId.toString(), ChatMessageSerializer.messagesToJson(messages));
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        memoryMapper.deleteMemory(memoryId.toString());
    }
}
