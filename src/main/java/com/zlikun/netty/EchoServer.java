package com.zlikun.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoServer {

    public static void main(String[] args) throws InterruptedException {

        new EchoServer().start(6666);

    }

    void start(int port) throws InterruptedException {
        // 事件循环组
        EventLoopGroup group = new NioEventLoopGroup();
        // 服务引导实例
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 设置事件循环组
        bootstrap.group(group)
                // 指定 NIO Channel 实现
                .channel(NioServerSocketChannel.class)
                // 用指定端口设置套接字地址
                .localAddress(port)
                // 添加 ChannelHandler 到子 Channel 的 ChannelPipeline
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoServerHandler());
                    }
                });

        log.info("Echo Server is running, listen 6666.");

        try {
            // 异步绑定服务器，调用 sync() 方法阻塞，等待绑定完成
            ChannelFuture future = bootstrap.bind().sync();
            // 获取 Channel 的 CloseFuture，并阻塞当前线程直到完成
            future.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup 释放所有资源
            group.shutdownGracefully().sync();
        }

    }

    /**
     * 入站事件处理器，@Sharable 注解表示对于所有客户端可以共用一个 EchoChannelHandler 实例
     *
     * @see io.netty.channel.ChannelHandler.Sharable
     */
    @ChannelHandler.Sharable
    class EchoServerHandler extends ChannelInboundHandlerAdapter {

        /**
         * 读取消息
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            // 读取客户端消息
            log.info("[{}]: {}", ctx.channel().remoteAddress(), buf.toString(CharsetUtil.UTF_8));
            // 将相同消息定写回客户端，注意此时并未刷新出站消息，除非手动flush
            // ctx.writeAndFlush(buf);  // 下面两个函数的简化调用方式
            ctx.write(buf);
            // ctx.flush();
        }

        /**
         * 最后一次读取消息
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            // 将未决消息刷新到远程节点（客户端），并关闭该Channel（所以一个客户端只能发送一次消息）
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        /**
         * 异常处理
         *
         * @param ctx
         * @param cause
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("与客户端[{}]通信出错", ctx.channel().remoteAddress(), cause);
            ctx.close();
        }
    }

}
