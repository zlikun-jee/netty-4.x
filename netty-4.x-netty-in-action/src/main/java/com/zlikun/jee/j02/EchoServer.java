package com.zlikun.jee.j02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 12:21
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {

        new EchoServer(1234).start();

    }

    public void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 创建 EventLoopGroup （事件循环组）
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 创建 ServerBootstrap
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    // 指定使用的NIO传输Channel
                    .channel(NioServerSocketChannel.class)
                    // 指定端口，设置套接字地址
                    .localAddress(new InetSocketAddress(this.port))
                    // 添加一个EchoServerHandler到子Channel的ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // EchoServerHandler被标记为@Shareable所以可以总是使用相同实例
                            // 即这个实例被所有客户端使用（无状态？）
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            // 异步绑定服务器，调用sync()方法阻塞，直到绑定完成
            ChannelFuture future = bootstrap.bind().sync();
            // 获取Channel的CloseFuture，并且阻塞当前线程直到完成
            future.channel().closeFuture().sync();
        } finally {
            // 关闭EventLoopGroup，释放资源
            group.shutdownGracefully().sync();
        }
    }

}
