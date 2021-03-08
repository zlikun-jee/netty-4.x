package com.zlikun.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortToByteEncoderTest {

    @Test
    void test() {

        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(1);
        buf.writeShort(2);
        buf.writeShort(3);
        ByteBuf in = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new ShortToByteEncoder());
        assertTrue(channel.writeOutbound(in));
        assertTrue(channel.finish());

        ByteBuf out = channel.readOutbound();
        // 直接比较 short 值
        assertEquals(Short.valueOf((short) 1), out.readShort());
        assertEquals(Short.valueOf((short) 2), out.readShort());
        // 也可比较 byte 值
        assertEquals(buf.skipBytes(4).readBytes(2), out.readBytes(2));

        assertNull(channel.readOutbound());
        assertTrue(buf.release());

    }

}