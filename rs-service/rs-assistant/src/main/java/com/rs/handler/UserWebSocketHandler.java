package com.rs.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rs.client.RabbitClient;
import com.rs.manage.AssistantManage;
import com.rs.model.customer.User;
import com.rs.model.entity.Message;
import com.rs.util.MessageUtil;
import com.rs.util.ZSetUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.rs.constant.AssistantConstant.*;
import static com.rs.constant.AttributeKeyConstant.SESSION_ID;
import static com.rs.constant.AttributeKeyConstant.USER;
import static com.rs.constant.RedisAssistantKeyyConstant.ASSISTANT_POOL;
import static com.rs.constant.RedisAssistantKeyyConstant.ASSISTANT_SESSION;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class UserWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final StringRedisTemplate stringRedisTemplate;

    private final AssistantManage assistantManage;

    private final ZSetUtil zSetUtil;

    private final RabbitClient rabbitClient;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        User user = ctx.channel().attr(USER).get();
        String sessionId = ctx.channel().attr(SESSION_ID).get();
        assistantManage.addUser(String.valueOf(user.getId()), ctx.channel());
        // 分配客服
        String assistant = (String) zSetUtil.leastCountNode(ASSISTANT_POOL);
        distributeChannel(assistant, user, sessionId);
        super.handlerAdded(ctx);
    }

    /**
     * 读取消息并转发
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        if (msg instanceof PingWebSocketFrame) {
            // 自动回复 Pong
            ctx.writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
            return;
        }

        if (msg instanceof CloseWebSocketFrame) {
            // 关闭连接
            ctx.close();
            return;
        }

        String sessionId = ctx.channel().attr(SESSION_ID).get();
        User user = ctx.channel().attr(USER).get();

        String assistant = null;
        Channel channel = null;
        // 获取assistant 和 channel
        Object assistantObj = stringRedisTemplate.opsForHash().get(ASSISTANT_SESSION + sessionId, user.getId() + "");
        if (StrUtil.isNotBlank((CharSequence) assistantObj)) {
            assistant = String.valueOf(assistantObj);
            channel = assistantManage.getChannel(assistant);
        }

        // 转发消息
        String messageText = ((TextWebSocketFrame) msg).text();
        Message message = JSONUtil.toBean(messageText, Message.class);
        message.setTo(assistant);
        message.setFrom(user.getId() + "");
        messageText = JSONUtil.toJsonStr(message);
        if (Objects.equals(message.getType(), USER_TYPE)) {
            rabbitClient.sendMsg("rs.assistant.msg", "assistant.user", messageText);
        }else if (Objects.equals(message.getType(), ORDER_TYPE)) {
            rabbitClient.sendMsg("rs.assistant.msg", "assistant.order", messageText);
        }
        if (channel != null) {
            MessageUtil.sendMessage(channel, message);
        }
    }

    private void distributeChannel(String assistant, User user, String sessionId) {
        stringRedisTemplate.opsForHash()
                .put(
                        ASSISTANT_SESSION + sessionId,
                        user.getId() + "",
                        assistant
                );
        assistantManage.addSession(assistant, user.getId() + "");
        Channel channel = assistantManage.getChannel(assistant);
        MessageUtil.sendMessage(
                channel,
                "用户" + JSONUtil.toJsonStr(user) + "已接入客服" + assistant,
                SYSTEM_TYPE,
                user.getId() + "",
                assistant
        );
    }

    /**
     * 用户下线后续操作
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 获取到用户信息
        String sessionId = ctx.channel().attr(SESSION_ID).get();
        User user = ctx.channel().attr(USER).get();

        // 通知客服用户下线
        try {
            Object assistantObj = stringRedisTemplate.opsForHash().get(ASSISTANT_SESSION + sessionId, user.getId() + "");
            String assistant = String.valueOf(assistantObj);
            Channel agentChannel = assistantManage.getChannel(assistant);
            MessageUtil.sendMessage(agentChannel, "用户" + user.getUsername() + "下线", CLOSE_TYPE, user.getId() + "", assistant);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 移除用户channel与对话信息
        Channel userChannel = assistantManage.getUserChannel(user.getId() + "");
        assistantManage.removeUser(user.getId() + "");
        for (Map.Entry<String, List<String>> stringListEntry : assistantManage.getSessionMap().entrySet()) {
            stringListEntry.getValue().remove(user.getId() + "");
        }
        stringRedisTemplate.opsForHash().delete(ASSISTANT_SESSION + sessionId, user.getId() + "");

        // 想C端前端发送关闭消息
        userChannel.writeAndFlush(new CloseWebSocketFrame());
        super.channelInactive(ctx);
    }
}
