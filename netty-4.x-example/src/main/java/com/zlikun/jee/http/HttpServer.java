package com.zlikun.jee.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executors;

/**
 * 实现一个HTTP服务（HTTPS）
 *
 * @author zlikun
 * @date 2019/2/28 19:06
 */
public class HttpServer {

    public static void main(String[] args) throws InterruptedException {
        new HttpServer().start(false);  // http
        // new HttpServer().start(true);   // https
    }

    public void start(boolean ssl) throws InterruptedException {

        // Boss数量为1
        EventLoopGroup boss = new NioEventLoopGroup();
        // Worker默认为CPU核数 * 2
        // Executor指的是工作线程池，用于处理实际业务
        EventLoopGroup worker = new NioEventLoopGroup(4, Executors.newCachedThreadPool());

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .localAddress(ssl ? 443 : 80)
                .childHandler(new HttpChannelInitializer(ssl))
        ;
        try {
            ChannelFuture future = bootstrap.bind().sync();
            System.err.printf("Open your web browser and navigate to http%s://127.0.0.1/%n", ssl ? "s" : "");
            future.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
        }

    }

}
