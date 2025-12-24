package com.rs.util;

import cn.hutool.json.JSONUtil;
import com.rs.model.entity.Message;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class MessageUtil {
    public static void sendMessage(Channel channel, String content, String type, String from, String to) {
        Message message = new Message();
        message.setContent(content);
        message.setType(type);
        message.setFrom(from);
        message.setTo(to);
        sendMessage(channel, message);
    }

    public static void sendMessage(Channel channel, Message message) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(message)));
    }


}
