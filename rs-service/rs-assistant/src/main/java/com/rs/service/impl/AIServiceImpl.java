package com.rs.service.impl;

import com.rs.config.Agent;
import com.rs.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    @Autowired
    @Qualifier("railwayAgent")
    private Agent agent;

    @Override
    public Flux<String> chat(String sessionId, String message) {
        return agent.chat(sessionId, message);
    }
}
