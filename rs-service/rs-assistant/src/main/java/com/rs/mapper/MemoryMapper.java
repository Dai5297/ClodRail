package com.rs.mapper;

import com.rs.model.assistant.Memory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemoryMapper {

    Memory getMemory(String sessionId);

    void addMemory(Memory memory);

    void updateMemory(Memory memory);

    List<Memory> queryByAgentId(Long aLong);

    Memory getMemoryByAgentAndUserId(String agentId, String userId);
}
