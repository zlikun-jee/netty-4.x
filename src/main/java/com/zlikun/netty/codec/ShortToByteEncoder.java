package com.zlikun.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 其它类型转换为Byte类型编码器
 *
 * @see MessageToByteEncoder
 */
@Slf4j
public class ShortToByteEncoder extends MessageToByteEncoder<Short> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Short msg, ByteBuf out) throws Exception {
        log.info("Input: {}", msg);
        out.writeShort(msg);
    }
}
