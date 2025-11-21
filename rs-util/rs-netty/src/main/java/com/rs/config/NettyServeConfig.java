package com.rs.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServeConfig {

    public static final int DEFAULT_PORT = 8080;

    public static final String DEFAULT_HOST = "localhost";

    public void start(ChannelInitializer<SocketChannel> initializer) {
        start(0, initializer);
    }

    public void start(int port, ChannelInitializer<SocketChannel> initializer) {
        start(port, null, initializer);
    }

    public void start(int port, String host, ChannelInitializer<SocketChannel> initializer) {
        port = port == 0 ? DEFAULT_PORT : port;
        host = host == null ? DEFAULT_HOST : host;

        NioEventLoopGroup bossEventLoop = new NioEventLoopGroup(1);
        NioEventLoopGroup workerEventLoop = new NioEventLoopGroup();
        try {
            new ServerBootstrap()
                    .group(bossEventLoop, workerEventLoop)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer)
                    .bind(host, port)
                    .sync();
            log.info("Netty server started at port: {}", port);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
