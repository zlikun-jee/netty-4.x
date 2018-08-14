package com.zlikun.jee.j09;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Decoder实际是一个ChannelHandler接口（ChannelInboundHandlerAdapter类）实现类
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 17:11
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够字节可以被读取，以生成下一个帧
        while (in.readableBytes() >= frameLength) {
            // 读取指定长度字节，生成一个帧，添加到解码消息列表中
            out.add(in.readBytes(frameLength));
        }
    }
}
