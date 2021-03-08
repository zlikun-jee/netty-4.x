package com.zlikun.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 同时实现编码和解码（出站、入站）
 *
 * @see MessageToMessageCodec
 */
@Slf4j
public class IntegerToStringCodec extends MessageToMessageCodec<Integer, String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        // 编码：将String转换为Integer
        log.info("Encode -> Input: {}", msg);
        out.add(Integer.valueOf(msg));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        // 解码：将Integer转换为String
        log.info("Decode -> Input: {}", msg);
        out.add(String.valueOf(msg));
    }
}
