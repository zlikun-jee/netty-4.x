package com.zlikun.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class ToIntegerDecoder2  extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 可以不判断字节数，实现原理是：如果没有足够的字节可用时，#readInt()会抛出一个Error（实际是一个Signal类），
        // 其将在基类中被捕获并处理，当有更多字节可用时，该方法会再次被调用
        out.add(in.readInt());
    }
}
