package com.zlikun.jee.echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Echo 服务端 Handler
 *
 * @author zlikun
 * @date 2019/2/26 11:27
 */
@Slf4j
@ChannelHandler.Sharable            // 用于多个Channel安全共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        log.info("Server received: {}", buf.toString(CharsetUtil.UTF_8));
        // ctx.write(buf);             // 读到什么，写出什么（Echo服务）
        ctx.writeAndFlush(buf);     // 读到什么，写出什么（Echo服务）
    }

    /**
     * 批量读取消息时读取最后一条消息时调用
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 将未决消息刷新到远程节点，并且关闭该Channel（连接）
        // ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        // ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    /**
     * 连接建立时执行
     *
     * @param ctx
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.info("channel [{}] registered", ctx.name());
    }

    /**
     * 连接关闭时执行
     *
     * @param ctx
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("channel [{}] unregistered", ctx.name());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();    // 打印异常堆栈
        ctx.close();                // 关闭Channel
    }
}
