package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.rs.client.user.UserClient;
import com.rs.mapper.MemoryMapper;
import com.rs.model.assistant.Memory;
import com.rs.model.dto.response.AdminMemoryResDTO;
import com.rs.model.entity.MsgContent;
import com.rs.service.AdminMemoryService;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminMemoryServiceImpl implements AdminMemoryService {

    private final MemoryMapper memoryMapper;

    private final UserClient userClient;

    @Override
    public List<AdminMemoryResDTO> memoryList() {
        List<Memory> memoryList = memoryMapper.queryByAgentId(UserContext.get());
        List<Long> userIds = memoryList.stream().map(Memory::getUserId).toList();

        Map<Long, String> usernamedMap;
        try {
            usernamedMap = userClient.usernameList(userIds);
        } catch (Exception e) {
            // 降级处理：如果获取用户名失败，使用空Map
            usernamedMap = Map.of();
        }

        Map<Long, String> finalUsernamedMap = usernamedMap;
        return memoryList.stream().map(memory -> {
            AdminMemoryResDTO adminMemoryResDTO = new AdminMemoryResDTO();
            BeanUtil.copyProperties(memory, adminMemoryResDTO);
            adminMemoryResDTO
                    .setUserName(finalUsernamedMap.getOrDefault(memory.getUserId(), "用户" + memory.getUserId()));
            List<MsgContent> contents = JSONUtil.toList(memory.getContent(), MsgContent.class);
            if (!contents.isEmpty()) {
                String content = contents.get(contents.size() - 1).getContent();
                adminMemoryResDTO.setLastContent(content);
            }
            adminMemoryResDTO.setSessionId(memory.getSessionId());
            return adminMemoryResDTO;
        }).toList();
    }

    @Override
    public AdminMemoryResDTO getMemoryDetail(String sessionId) {
        Memory memory = memoryMapper.getMemory(sessionId);
        if (memory == null) {
            return null;
        }

        AdminMemoryResDTO adminMemoryResDTO = new AdminMemoryResDTO();
        BeanUtil.copyProperties(memory, adminMemoryResDTO);

        // 获取用户名
        try {
            Map<Long, String> usernamedMap = userClient.usernameList(List.of(memory.getUserId()));
            adminMemoryResDTO.setUserName(usernamedMap.get(memory.getUserId()));
        } catch (Exception e) {
            adminMemoryResDTO.setUserName("用户" + memory.getUserId());
        }

        // 获取最后一条消息
        List<MsgContent> contents = JSONUtil.toList(memory.getContent(), MsgContent.class);
        if (!contents.isEmpty()) {
            String content = contents.get(contents.size() - 1).getContent();
            adminMemoryResDTO.setLastContent(content);
        }

        adminMemoryResDTO.setContent(memory.getContent());
        adminMemoryResDTO.setSessionId(memory.getSessionId());
        return adminMemoryResDTO;
    }
}
