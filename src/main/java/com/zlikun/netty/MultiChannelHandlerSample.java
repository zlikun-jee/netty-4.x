package com.zlikun.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Pipeline上添加多个ChannelHandler，测试出站、入站流程
 */
@Slf4j
public class MultiChannelHandlerSample {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(6666)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new FirstChannelHandler())
                                .addLast(new SecondChannelHandler())
                                .addLast(new ThirdChannelHandler());
                    }
                });

        log.info("启动服务...");

        try {
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }

    static class FirstChannelHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("In First ChannelHandler");
            // 向下传递
            ctx.fireChannelActive();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            String s = buf.toString(CharsetUtil.UTF_8);
            log.info("First Read: {}", s);
            // 模拟抛出异常的情况
            if (Objects.equals(s, "error")) throw new Exception("ERROR");
            // 回写消息
            ctx.write(Unpooled.wrappedBuffer("First".getBytes(StandardCharsets.UTF_8)));

            // 默认消息不会向下传递，需要通过 #fire* 系方法来触发
            ctx.fireChannelRead(msg);

            // 这里不手动释放缓冲区，留给下一Handler来处理
            // ReferenceCountUtil.release(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // 只记录异常，并不关闭 channel，模拟向下传递异常情形
            log.error("First Error", cause);
            // 向下传递
            ctx.fireExceptionCaught(cause);
        }
    }

    static class SecondChannelHandler extends SimpleChannelInboundHandler<Object> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("In Second ChannelHandler");
        }

        /**
         * SimpleChannelInboundHandler#channelRead() 会自动释放缓冲区
         *
         * @param ctx
         * @param o
         * @throws Exception
         * @see SimpleChannelInboundHandler#channelRead(ChannelHandlerContext, Object)
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
            ByteBuf buf = (ByteBuf) o;
            String s = buf.toString(CharsetUtil.UTF_8);
            log.info("Second Read: {}", s);
            ChannelFuture future = ctx.writeAndFlush(Unpooled.wrappedBuffer("Second".getBytes(StandardCharsets.UTF_8)));
            // 当接收消息为 quit 时关闭Channel
            if (Objects.equals(s, "quit")) {
                future.addListener(ChannelFutureListener.CLOSE);
                log.info("QUIT...");
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("Second Error", cause);
            ctx.close();
        }
    }

    /**
     * 出站消息 ChannelHandler
     */
    static class ThirdChannelHandler extends ChannelOutboundHandlerAdapter {
        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            super.close(ctx, promise);
            log.info("Third Close");
        }
    }

}

