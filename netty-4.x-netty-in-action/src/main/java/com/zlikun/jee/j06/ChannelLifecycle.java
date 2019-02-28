package com.zlikun.jee.j06;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * 6.1.1 Channel生命周期 <br/>
 * 6.1.2 ChannelHandler生命周期 <br/>
 *
 * @author zlikun
 * @date 2019/2/28 13:20
 */
public class ChannelLifecycle {

    /**
     * 尝试用telnet连接EchoServer，一次完整的交互输出如下
     * $ telnet 10.10.10.42 1234
     * Trying 10.10.10.42...
     * Connected to 10.10.10.42.
     * Escape character is '^]'.
     * xxx
     * xxx
     * Connection closed by foreign host.
     *
     * <pre>
     * A. handlerAdded
     * 2. channelRegistered
     * 3. channelActive
     * Receive data: xxx
     *
     * 4. channelInactive
     * 1. channelUnregistered
     * B. handlerRemoved
     * </pre>
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(1234)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelHandlerAdapter channelHandlerAdapter = new MyChannelHandler();
                        // 检查是否被标记为：@ChannelHandler.Sharable
                        if (channelHandlerAdapter.isSharable()) {
                            ch.pipeline().addLast("lifecycle", channelHandlerAdapter);
                        }
                    }
                });
        try {
            bootstrap.bind().sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }

    @ChannelHandler.Sharable
    static class MyChannelHandler extends ChannelInboundHandlerAdapter {

        /**
         * 1. Channel已经创建，但还未注册到EventLoop
         *
         * @param ctx
         */
        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) {
            System.out.println("1. channelUnregistered");
        }

        /**
         * 2. Channel已经被注册到了EventLoop
         *
         * @param ctx
         */
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) {
            System.out.println("2. channelRegistered");
        }

        /**
         * 3. Channel处于活动状态（已连接到它的远程节点），可以接收发送数据了
         *
         * @param ctx
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println("3. channelActive");
        }

        /**
         * 可以接收和发送数据了
         *
         * @param ctx
         * @param msg
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf buf = (ByteBuf) msg;
            // 接收数据
            System.out.println("Receive data: " + buf.toString(CharsetUtil.UTF_8));
            // 发送数据
            ctx.writeAndFlush(buf);
            // 丢弃已接收的消息
            // ReferenceCountUtil.release(buf);
        }

        /**
         * 消息读完后关闭连接（EchoServer）
         *
         * @param ctx
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        /**
         * 4. Channel没有连接到远程节点
         *
         * @param ctx
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            System.out.println("4. channelInactive");
        }

        /**
         * A. 当把ChannelHandler添加到ChannelPipeline中时调用
         *
         * @param ctx
         */
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            System.out.println("A. handlerAdded");
        }

        /**
         * B. 当把ChannelHandler从ChannelPipeline中移除时调用
         *
         * @param ctx
         */
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            System.out.println("B. handlerRemoved");
        }

        /**
         * C. 当处理过程中在ChannelPipeline中有错误时调用
         *
         * @param ctx
         * @param cause
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.out.println("C. exceptionCaught");
            cause.printStackTrace();
            ctx.close();
        }
    }

}
