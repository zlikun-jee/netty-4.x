package com.zlikun.jee;

import com.zlikun.jee.handler.RequestRouteHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 自实现一个HTTP服务器
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 19:53
 */
public class HttpServer {

    /**
     * 在指定端口上启动服务
     *
     * @param port
     * @throws InterruptedException
     */
    public void start(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 请求解码器
                            pipeline.addLast("decoder", new HttpRequestDecoder());
                            // 将多个消息转换为单一的Request或Response对象
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            // 响应编码器
                            pipeline.addLast("encoder", new HttpResponseEncoder());
                            // 支持大文件传输
                            pipeline.addLast("chunked", new ChunkedWriteHandler());
                            // 自实业请求处理器
                            // pipeline.addLast(new HttpRequestHandler());
                            pipeline.addLast(new RequestRouteHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Http Server is running ...");

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new HttpServer().start(80);
    }

}
