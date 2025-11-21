package com.rs.handler;

import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private final AgentWebSocketHandler agentWebSocketHandler;

    private final UserWebSocketHandler userWebSocketHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (!req.decoderResult().isSuccess() || !("websocket".equalsIgnoreCase(req.headers().get("Upgrade")))) {
            throw new CommonException(RespCode.ERROR, "非法请求");
        }

        String uri = req.uri();
        if (!uri.startsWith("/ws/assistant/user") && !uri.startsWith("/ws/assistant/agent")) {
            throw new RuntimeException("未知请求");
        }

        String wsUrl = "ws://" + req.headers().get(HttpHeaderNames.HOST) + uri;
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(wsUrl, null, true, 65536);
        WebSocketServerHandshaker handshaker = factory.newHandshaker(req);

        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
            handshakeFuture.addListener(future -> {
                if (future.isSuccess()) {
                    ChannelPipeline pipeline = ctx.pipeline();
                    if (pipeline.get(WebSocketHandler.class) != null) {
                        pipeline.remove(this);
                    }

                    pipeline.addLast(new WebSocketFrameAggregator(65536));
                    if (uri.startsWith("/ws/assistant/user")) {
                        pipeline.addLast(userWebSocketHandler);
                    }else if (uri.startsWith("/ws/assistant/agent")) {
                        pipeline.addLast(agentWebSocketHandler);
                    }
                }else {
                    throw new CommonException(RespCode.ERROR, "系统错误");
                }
            });
        }
    }
}
