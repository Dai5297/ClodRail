package com.rs.service;

import reactor.core.publisher.Flux;

public interface AIService {

    Flux<String> chat(String sessionId, String message);
}
