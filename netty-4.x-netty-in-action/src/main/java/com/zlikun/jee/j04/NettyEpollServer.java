package com.zlikun.jee.j04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

/**
 * @author zlikun
 * @date 2019/2/27 17:53
 */
public class NettyEpollServer implements NettyServer {

    @Override
    public void server(int port) throws InterruptedException {

        EventLoopGroup group = new EpollEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(EpollServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
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
