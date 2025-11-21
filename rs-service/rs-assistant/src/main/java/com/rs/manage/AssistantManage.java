package com.rs.manage;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AssistantManage {

    private final ConcurrentHashMap<String, Channel> agentChannelMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    public void addAgent(String agentId, Channel channel) {
        agentChannelMap.put(agentId, channel);
    }

    public Channel getChannel(String agentId) {
        return agentChannelMap.get(agentId);
    }

    public void removeAgent(String agentId) {
        agentChannelMap.remove(agentId);
    }

    public boolean isOnline(String agentId) {
        Channel ch = agentChannelMap.get(agentId);
        return ch != null && ch.isActive();
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

    public boolean isUserOnline(String userId) {
        Channel ch = userChannelMap.get(userId);
        return ch != null && ch.isActive();
    }
}
