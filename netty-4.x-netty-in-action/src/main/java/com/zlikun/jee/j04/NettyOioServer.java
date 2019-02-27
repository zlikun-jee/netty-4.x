package com.zlikun.jee.j04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

/**
 * 使用Netty实现一个OIO版本的服务器，再实现一个NIO版本的服务器，可以看出两者代码结构基本一致<br>
 * 如果使用原生JDK实现，则两者代码差别和很大
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 13:29
 */
public class NettyOioServer implements NettyServer {

    @Override
    public void server(int port) throws InterruptedException {
        EventLoopGroup group = new OioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(OioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HiChannelHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
