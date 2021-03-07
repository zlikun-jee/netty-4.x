package com.zlikun.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedLengthFrameDecoderTest {

    @Test
    void test() {

        int len = 3;
        int loop = 4;

        // 准备数据
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < len * loop; i++) {
            buf.writeByte(i);
        }

        ByteBuf input = buf.duplicate();

        // 准备测试
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(len));
        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());

        // Read messages
        ByteBuf read;
        for (int i = 0; i < loop; i++) {
            read = channel.readInbound();
            assertEquals(buf.readSlice(len), read);
            read.release();
        }

        assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    void test2() {
        int len = 3, loop = 4;

        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < len * loop; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(len));
        assertFalse(channel.writeInbound(input.readBytes(2)));  // 不够一帧
        assertTrue(channel.writeInbound(input.readBytes(10)));
        assertTrue(channel.finish());

        ByteBuf read;
        for (int i = 0; i < loop; i++) {
            read = channel.readInbound();
            assertEquals(buf.readSlice(len), read);
            read.release();
        }

        assertNull(channel.readInbound());
        buf.release();

    }

}