package com.zlikun.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerToStringCodecTest {

    @Test
    void test() {

        ByteBuf inBuf = Unpooled.buffer();
        inBuf.writeInt(1);
        inBuf.writeInt(2);
        inBuf.writeInt(3);
        ByteBuf in = inBuf.duplicate();

        ByteBuf outBuf = Unpooled.buffer();
        outBuf.writeInt(128);
        outBuf.writeInt(256);
        outBuf.writeInt(512);
        ByteBuf out = outBuf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new IntegerToStringCodec());
        assertTrue(channel.writeInbound(in));
        assertTrue(channel.writeOutbound(out));
        assertTrue(channel.finish());

        // 测试入站消息
        ByteBuf inboundBuf = channel.readInbound();
        assertEquals(inBuf.readInt(), inboundBuf.readInt());
        assertEquals(inBuf.readInt(), inboundBuf.readInt());
        assertEquals(inBuf.readInt(), inboundBuf.readInt());
        assertNull(channel.readInbound());

        // 测试出站消息
        ByteBuf outboundBuf = channel.readOutbound();
        assertEquals(outBuf.readBytes(4), outboundBuf.readBytes(4));
        assertEquals(outBuf.readBytes(4), outboundBuf.readBytes(4));
        assertEquals(outBuf.readInt(), outboundBuf.readInt());
        assertNull(channel.readOutbound());

        assertTrue(inBuf.release());
        assertTrue(outBuf.release());

    }

}