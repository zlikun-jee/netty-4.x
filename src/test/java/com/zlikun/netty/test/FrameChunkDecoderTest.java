package com.zlikun.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FrameChunkDecoderTest {

    @Test
    void test() {

        int maxFrameSize = 3;

        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }

        ByteBuf in = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(maxFrameSize));
        // 写入2字节正常
        assertTrue(channel.writeInbound(in.readBytes(2)));
        // 写入4字节超限
        try {
            channel.writeInbound(in.readBytes(4));
        } catch (TooLongFrameException e) {
            // fail(e);
            log.error("Error ->", e);
        }

        // 写入剩余字节
        assertTrue(channel.writeInbound(in.readBytes(3)));
        assertTrue(channel.finish());

        // Read frames
        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(2), read);

        // 失败的字节将被跳过
        read = channel.readInbound();
        assertEquals(buf.skipBytes(4).readSlice(3), read);
        read.release();
        buf.release();

    }

}