package com.rs.config;

import com.rs.handler.TokenHandler;
import com.rs.handler.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NettyConfig {

    private NettyServeConfig nettyServeConfig = new NettyServeConfig();

    private final WebSocketHandler webSocketHandler;

    private final TokenHandler tokenHandler;

    @PostConstruct
    public void nettyServeConfig() {
        new Thread(() -> {
            try {
                log.info("=== 启动 Netty WebSocket 服务器 ===");
                nettyServeConfig.start(18085, "0.0.0.0",  // 绑定所有网卡
                        new ChannelInitializer<>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                log.info("✅ 新连接建立: {}", ch.remoteAddress());
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new HttpServerCodec());
                                pipeline.addLast(new HttpObjectAggregator(65536));
                                pipeline.addLast(tokenHandler);
                                pipeline.addLast(webSocketHandler);
                            }
                        }
                );
            } catch (Exception e) {
                log.error("❌ Netty 启动失败", e);
                throw new RuntimeException(e);
            }
        }, "Netty-Server").start();
    }

}
