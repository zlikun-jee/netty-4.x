package com.zlikun.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketServer {

    public static void main(String[] args) throws InterruptedException {
        // 创建 DefaultChannelGroup 其将保存所有已经连接的 WebSocket Channel
        ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

        EventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(7777)
                .childHandler(new WebSocketInitializer(channelGroup));

        log.info("Server is running.");

//        try {
//            ChannelFuture future = bootstrap.bind().sync();
//            future.channel().closeFuture().sync();
//        } finally {
//            group.shutdownGracefully().sync();
//        }



    }

}
