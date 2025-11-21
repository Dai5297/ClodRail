package com.rs.handler;

import com.rs.constant.RedisAssistantKeyyConstant;
import com.rs.manage.AssistantManage;
import com.rs.model.entity.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.rs.constant.AssistantConstant.ASSISTANT_TYPE;
import static com.rs.constant.AttributeKeyConstant.AGENT;
import static com.rs.constant.AttributeKeyConstant.SESSION_ID;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class AgentWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final StringRedisTemplate stringRedisTemplate;

    private final AssistantManage assistantManage;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // TODO 改为Admin类
        Object arg = ctx.channel().attr(AGENT).get();
        log.info("客服：{}上线", arg);
        // 向Redis的客服池中添加当前客服
//        stringRedisTemplate.opsForZSet().add(RedisAssistantKeyyConstant.ASSISTANT_POOL, (String) arg, 1);
        // 向客服Manage中添加 客服-channel 对应关系
        // TODO 改为Admin类的Id
        assistantManage.addAgent("1", ctx.channel());
        super.handlerAdded(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if (msg instanceof PingWebSocketFrame) {
            // 自动回复 Pong
            ctx.writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
            return;
        }

        // 解析msg
        String sessionId = ctx.channel().attr(SESSION_ID).get();
        Set<Object> fields = stringRedisTemplate.opsForHash().keys(RedisAssistantKeyyConstant.ASSISTANT_SESSION + sessionId);

        // 转为 List<String>
        List<String> userIds = fields.stream().map(String::valueOf).collect(Collectors.toList());
        String userId = userIds.get(0);

        Channel userChannel = assistantManage.getUserChannel(userId);
        String messageText = ((TextWebSocketFrame) msg).text();
        Message sendMessage = new Message();
        sendMessage.setType(ASSISTANT_TYPE);
        // TODO 将 1 改为客服的Id
        sendMessage.setFrom("1");
        sendMessage.setTo(userId);
        sendMessage.setContent(messageText);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(sendMessage.toString());
        userChannel.writeAndFlush(textWebSocketFrame);
    }
}
