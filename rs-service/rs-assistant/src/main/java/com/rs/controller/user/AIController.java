package com.rs.controller.user;

import com.rs.annotation.IgnoreResult;
import com.rs.model.dto.request.AIChatReqDTO;
import com.rs.model.dto.response.ChatMessageResDTO;
import com.rs.model.dto.response.ChatSessionResDTO;
import com.rs.model.dto.response.SessionCreateResDTO;
import com.rs.service.AIService;
import com.rs.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "用户客服接口")
@RequestMapping("/assistant")
public class AIController {

    private final AIService aiService;
    private final SessionService sessionService;

    @IgnoreResult
    @PostMapping("/chat")
    @Operation(summary = "AI智能问答（流式响应）")
    public Flux<String> chat(@RequestBody AIChatReqDTO chatReqDTO) {
        return aiService.chat(chatReqDTO.getMemoryId(), chatReqDTO.getMessage());
    }

    @GetMapping("/sessions")
    @Operation(summary = "获取会话历史列表")
    public List<ChatSessionResDTO> getSessions() {
        return sessionService.getSessions();
    }

    @GetMapping("/history/{sessionId}")
    @Operation(summary = "获取会话消息历史")
    public List<ChatMessageResDTO> getChatHistory(@PathVariable String sessionId) {
        return sessionService.getChatHistory(sessionId);
    }

    @PostMapping("/session")
    @Operation(summary = "创建新会话")
    public SessionCreateResDTO createSession() {
        return sessionService.createSession();
    }

    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "删除会话")
    public void deleteSession(@PathVariable String sessionId) {
        sessionService.deleteSession(sessionId);
    }
}
