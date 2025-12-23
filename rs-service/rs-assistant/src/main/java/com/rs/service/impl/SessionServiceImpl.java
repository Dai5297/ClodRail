package com.rs.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.SessionMapper;
import com.rs.model.dto.response.ChatMessageResDTO;
import com.rs.model.dto.response.ChatSessionResDTO;
import com.rs.model.dto.response.SessionCreateResDTO;
import com.rs.model.assistant.Memory;
import com.rs.service.SessionService;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 会话管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionMapper sessionMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<ChatSessionResDTO> getSessions() {
        Long userId = UserContext.get();

        List<Memory> memories = sessionMapper.listSessionsByUserId(userId);
        List<ChatSessionResDTO> sessions = new ArrayList<>();

        for (Memory memory : memories) {
            ChatSessionResDTO session = new ChatSessionResDTO();
            session.setSessionId(memory.getSessionId());
            session.setType("ai");

            // 解析内容获取最后一条消息和标题
            String content = memory.getContent();
            if (StrUtil.isNotBlank(content)) {
                try {
                    JSONArray messages = JSONUtil.parseArray(content);
                    if (!messages.isEmpty()) {
                        JSONObject lastMsg = messages.getJSONObject(messages.size() - 1);
                        String lastMsgContent = lastMsg.getStr("text", "");
                        session.setLastMessage(lastMsgContent.length() > 30
                                ? lastMsgContent.substring(0, 30) + "..."
                                : lastMsgContent);

                        // 获取第一条用户消息作为标题
                        String title = "新对话";
                        for (int i = 0; i < messages.size(); i++) {
                            JSONObject msg = messages.getJSONObject(i);
                            if ("USER".equals(msg.getStr("type"))) {
                                String msgContent = msg.getStr("text", "");
                                title = msgContent.length() > 20
                                        ? msgContent.substring(0, 20) + "..."
                                        : msgContent;
                                break;
                            }
                        }
                        session.setTitle(title);
                    }
                } catch (Exception e) {
                    log.warn("解析会话内容失败: {}", e.getMessage());
                    session.setLastMessage("");
                    session.setTitle("对话");
                }
            } else {
                session.setLastMessage("");
                session.setTitle("新对话");
            }

            session.setUpdateTime(LocalDateTime.now());
            session.setCreateTime(LocalDateTime.now());
            sessions.add(session);
        }

        return sessions;
    }

    @Override
    public List<ChatMessageResDTO> getChatHistory(String sessionId) {
        Long userId = UserContext.get();

        // 验证会话所属权
        if (!sessionId.startsWith(userId + "_")) {
            throw new CommonException(RespCode.UNAUTHORIZED, "无权访问此会话");
        }

        Memory memory = sessionMapper.getSessionById(sessionId);
        if (memory == null) {
            return new ArrayList<>();
        }

        List<ChatMessageResDTO> messages = new ArrayList<>();
        String content = memory.getContent();

        if (StrUtil.isNotBlank(content)) {
            try {
                JSONArray jsonArray = JSONUtil.parseArray(content);
                long id = 1;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonMsg = jsonArray.getJSONObject(i);

                    // 过滤掉SYSTEM类型的消息（系统消息不需要显示）
                    String type = jsonMsg.getStr("type", "USER");
                    if ("SYSTEM".equals(type)) {
                        continue;
                    }

                    ChatMessageResDTO message = new ChatMessageResDTO();
                    message.setId(id++);

                    // 转换消息类型
                    message.setType("USER".equals(type) ? "user" : "ai");
                    if ("USER".equals(type)) {
                        JSONArray contents = (JSONArray) jsonMsg.get("contents");
                        if (contents != null && !contents.isEmpty()) {
                            Object first = contents.get(0);
                            if (first instanceof JSONObject userMsg) {
                                Object contentType = userMsg.get("type");
                                if (contentType != null && "TEXT".equals(String.valueOf(contentType))) {
                                    message.setContent(userMsg.getStr("text", ""));
                                } else {
                                    message.setContent(userMsg.getStr("text", jsonMsg.getStr("text", "")));
                                }
                            } else {
                                message.setContent(jsonMsg.getStr("text", ""));
                            }
                        } else {
                            message.setContent(jsonMsg.getStr("text", ""));
                        }
                    } else {
                        message.setContent(jsonMsg.getStr("text", ""));
                    }

                    // 解析时间戳
                    String timestamp = jsonMsg.getStr("timestamp");
                    if (StrUtil.isNotBlank(timestamp)) {
                        try {
                            message.setTimestamp(LocalDateTime.parse(timestamp, FORMATTER));
                        } catch (Exception e) {
                            message.setTimestamp(LocalDateTime.now());
                        }
                    } else {
                        message.setTimestamp(LocalDateTime.now());
                    }

                    messages.add(message);
                }
            } catch (Exception e) {
                log.error("解析会话历史失败: {}", e.getMessage(), e);
            }
        }

        return messages;
    }

    @Override
    public SessionCreateResDTO createSession() {
        Long userId = UserContext.get();

        // 生成会话ID：userId_timestamp
        String sessionId = userId + "_" + System.currentTimeMillis();

        Memory memory = new Memory();
        memory.setSessionId(sessionId);
        memory.setContent("[]"); // 初始化为空的消息数组

        sessionMapper.createSession(memory);

        SessionCreateResDTO response = new SessionCreateResDTO();
        response.setSessionId(sessionId);
        response.setTitle("新对话");
        response.setType("ai");
        response.setCreateTime(LocalDateTime.now());

        return response;
    }

    @Override
    public void deleteSession(String sessionId) {
        Long userId = UserContext.get();

        // 验证会话所属权
        if (!sessionId.startsWith(userId + "_")) {
            throw new CommonException(RespCode.UNAUTHORIZED, "无权删除此会话");
        }

        int rows = sessionMapper.deleteSession(sessionId, userId);
        if (rows == 0) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "会话不存在或已被删除");
        }
    }
}
