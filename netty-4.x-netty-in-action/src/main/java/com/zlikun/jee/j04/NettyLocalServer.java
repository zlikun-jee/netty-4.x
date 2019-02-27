package com.zlikun.jee.j04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;

/**
 * @author zlikun
 * @date 2019/2/27 18:01
 */
public class NettyLocalServer implements NettyServer {
    @Override
    public void server(int port) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup group = new DefaultEventLoopGroup();
        bootstrap.group(group)
                .channel(LocalServerChannel.class)
                .localAddress(new LocalAddress("vm-local-address"))
                .childHandler(new ChannelInitializer<ServerChannel>() {
                    @Override
                    protected void initChannel(ServerChannel ch) {
                        ch.pipeline().addLast(new HiChannelHandler());
                    }
                });

        try {
            bootstrap.bind().sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
