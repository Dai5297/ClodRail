package com.rs.handler;

import com.rs.constant.RedisAssistantKeyyConstant;
import com.rs.manage.AssistantManage;
import com.rs.model.customer.User;
import com.rs.model.entity.Message;
import com.rs.util.ZSetUtil;
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

import static com.rs.constant.AssistantConstant.USER_TYPE;
import static com.rs.constant.AttributeKeyConstant.SESSION_ID;
import static com.rs.constant.AttributeKeyConstant.USER;
import static com.rs.constant.RedisAssistantKeyyConstant.ASSISTANT_POOL;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class UserWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final StringRedisTemplate stringRedisTemplate;

    private final AssistantManage assistantManage;

    private final ZSetUtil zSetUtil;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Long id = ctx.channel().attr(USER).get().getId();
        assistantManage.addUser(String.valueOf(id), ctx.channel());
        super.handlerAdded(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if (msg instanceof PingWebSocketFrame) {
            // 自动回复 Pong
            ctx.writeAndFlush(new PongWebSocketFrame(msg.content().retain()));
            return;
        }
        String sessionId = ctx.channel().attr(SESSION_ID).get();
        User user = ctx.channel().attr(USER).get();
        // 判断当前用户是否已经分配了客服
        String assistant = String.valueOf(
                stringRedisTemplate.opsForHash()
                        .get(
                                RedisAssistantKeyyConstant.ASSISTANT_SESSION + sessionId,
                                user.getId() + ""
                        )
        );
        Channel channel = null;
        if (assistant == null) {
            // 为用户分配客服并获取对应的channel
            assistant = (String) zSetUtil.leastCountNode(ASSISTANT_POOL);
            stringRedisTemplate.opsForHash()
                    .put(
                            RedisAssistantKeyyConstant.ASSISTANT_SESSION + sessionId,
                            user.getId() + "",
                            assistant
                    );
            channel = assistantManage.getChannel(assistant);
        } else {
            channel = assistantManage.getChannel(assistant);
        }
        String messageText = ((TextWebSocketFrame) msg).text();
        Message message = new Message();
        message.setType(USER_TYPE);
        message.setFrom(user.getId() + "");
        message.setTo(assistant);
        message.setContent(messageText);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(message.toString());
        channel.writeAndFlush(textWebSocketFrame);
    }
}
