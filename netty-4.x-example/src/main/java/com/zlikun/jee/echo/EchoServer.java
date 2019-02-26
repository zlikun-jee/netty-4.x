package com.zlikun.jee.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author zlikun
 * @date 2019/2/26 11:40
 */
public class EchoServer {

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    /**
     * 使用 telnet 测试 Echo 服务
     * $ telnet 10.10.10.42 1234
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        new EchoServer(1234).start();
    }

    public void start() throws InterruptedException {

        // 针对Linux系统使用系统的Epoll IO模型，非Linux使用Java的NIO模型
        final boolean isLinux = isLinux();
        EventLoopGroup group = null;
        if (isLinux) {
            group = new EpollEventLoopGroup();
        } else {
            group = new NioEventLoopGroup();
        }

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(isLinux ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .localAddress("0.0.0.0", this.port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoServerHandler());
                    }
                })
        ;

        try {
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅关机
            group.shutdownGracefully().sync();
        }

    }

    /**
     * 判断当前操作系统是否是Linux系统
     *
     * @return
     */
    private boolean isLinux() {
        // Windows 10
        // Linux
        final String osName = System.getProperty("os.name");
        return osName != null && osName.equalsIgnoreCase("Linux");
    }

}
