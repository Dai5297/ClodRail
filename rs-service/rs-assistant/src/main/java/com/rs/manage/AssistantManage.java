package com.rs.manage;

import io.netty.channel.Channel;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Component
public class AssistantManage {

    private final ConcurrentHashMap<String, Channel> agentChannelMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, List<String>> sessionMap = new ConcurrentHashMap<>();

    public void addAgent(String agentId, Channel channel) {
        agentChannelMap.put(agentId, channel);
    }

    public Channel getChannel(String agentId) {
        return agentChannelMap.get(agentId);
    }

    public void removeAgent(String agentId) {
        agentChannelMap.remove(agentId);
    }

    public void addUser(String userId, Channel channel) {
        userChannelMap.put(userId, channel);
    }

    public Channel getUserChannel(String userId) {
        return userChannelMap.get(userId);
    }

    public void removeUser(String userId) {
        userChannelMap.remove(userId);
    }

    public void addSession(String assistantId, String userId) {
        sessionMap.computeIfAbsent(
                assistantId, k -> new CopyOnWriteArrayList<>()
        ).add(userId);
    }

    public void removeSession(String assistantId) {
        sessionMap.remove(assistantId);
    }

    public Integer countOnlineAgent() {
        return agentChannelMap.size();
    }
}
