package com.zlikun.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbsIntegerEncoderTest {

    @Test
    void test() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        assertTrue(channel.writeOutbound(buf));
        assertTrue(channel.finish());

        // Read bytes
        for (int i = 1; i < 10; i++) {
            assertEquals(Integer.valueOf(i), channel.readOutbound());
        }

        assertNull(channel.readOutbound());

        // 出站Handler一般不用手动释放，会在Pipeline的HeadHandler中自动释放
        // 如果手动释放需要判断引用计数是否大于0
        if (buf.refCnt() > 0) {
            assertTrue(buf.release());
        }
    }

}