package com.zlikun.jee.j04;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author zlikun
 * @date 2019/2/27 17:58
 */
public class HiChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi\r\n", CharsetUtil.UTF_8));
        ctx.writeAndFlush(buf.duplicate())
                .addListener(ChannelFutureListener.CLOSE);
    }
}
