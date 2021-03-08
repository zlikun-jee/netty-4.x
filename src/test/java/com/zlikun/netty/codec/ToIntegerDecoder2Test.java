package com.zlikun.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToIntegerDecoder2Test {

    @Test
    void test() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeInt(i);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new ToIntegerDecoder2());
        assertTrue(channel.writeInbound(buf));
        assertTrue(channel.finish());

        for (int i = 0; i < 9; i++) {
            assertEquals(Integer.valueOf(i), channel.readInbound());
        }

        assertNull(channel.readInbound());
    }

}