package com.rs.handler;

import cn.hutool.json.JSONUtil;
import com.rs.constant.RedisAssistantKeyyConstant;
import com.rs.manage.AssistantManage;
import com.rs.model.customer.Admin;
import com.rs.model.entity.Message;
import com.rs.util.MessageUtil;
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

import static com.rs.constant.AssistantConstant.CLOSE_TYPE;
import static com.rs.constant.AttributeKeyConstant.AGENT;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class AgentWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final StringRedisTemplate stringRedisTemplate;

    private final AssistantManage assistantManage;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Admin admin = (Admin) ctx.channel().attr(AGENT).get();
        log.info("客服：{}上线", admin);
        // 向Redis的客服池中添加当前客服
        stringRedisTemplate.opsForZSet().add(RedisAssistantKeyyConstant.ASSISTANT_POOL, admin.getId() + "", 1);
        // 向客服Manage中添加 客服-channel 对应关系
        assistantManage.addAgent(String.valueOf(admin.getId()), ctx.channel());
        super.handlerAdded(ctx);
    }

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

        Admin admin = ctx.channel().attr(AGENT).get();
        String messageJson = ((TextWebSocketFrame) msg).text();
        Message message = JSONUtil.toBean(messageJson, Message.class);
        if (CLOSE_TYPE.equals(message.getType())) {
            handleClose(admin, message);
        }
        message.setFrom(admin.getId() + "");
        String userId = message.getTo();
        Channel userChannel = assistantManage.getUserChannel(userId);
        MessageUtil.sendMessage(userChannel, message);
    }

    private void handleClose(Admin admin, Message message) {
        List<String> userIds = assistantManage.getSessionMap().get(admin.getId() + "");
        for (String userId : userIds) {
            message.setTo(userId);
            MessageUtil.sendMessage(assistantManage.getUserChannel(userId), message);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Admin admin = ctx.channel().attr(AGENT).get();
        assistantManage.removeAgent(admin.getId() + "");
        assistantManage.removeSession(admin.getId() + "");
        super.channelInactive(ctx);
    }
}
