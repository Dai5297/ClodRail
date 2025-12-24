package com.rs.config;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface Agent {

    Flux<String> chat(@MemoryId String memoryId, @UserMessage String message);
}
