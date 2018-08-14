package com.zlikun.jee.j02;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 通过实现ChannelInboundHandler接口来定义响应入站事件的方法，
 * 为了简化代码，这里通过继承了ChannelInboundHandler的子类ChannelInboundHandlerAdapter来实现，
 * 该类是一个Adapter，提供了一些默认实现
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 12:07
 */
@ChannelHandler.Sharable    // 标示一个 ChannelHandler 可以被多个 Channel 安全地共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 每条输入的消息都会调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 这里的实现为简单在控制台输出消息
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        // 将接收到的消息写给发送者，而不刷新出站消息
        ctx.write(in);
    }

    /**
     * 批量读取消息时读取最后一条消息时调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将未决消息刷新到远程节点，并且关闭该Channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 读取期间抛出异常时调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 打印异常堆栈
        cause.printStackTrace();
        // 关闭Channel
        ctx.close();
    }

}
