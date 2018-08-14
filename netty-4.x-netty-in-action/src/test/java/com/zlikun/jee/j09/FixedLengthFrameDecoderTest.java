package com.zlikun.jee.j09;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 17:15
 */
public class FixedLengthFrameDecoderTest {
    @Test
    public void decode() {

        // 创建一个ByteBuf，并存储9个字节[1, 9]
        ByteBuf buf = Unpooled.buffer();
        Stream.iterate(1, i -> ++i).limit(9).forEach(buf::writeByte);

        // 创建一个副本（视图）
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        // 将数据写入Channel
        assertTrue(channel.writeInbound(input.retain()));
        // 标记channel为已完成状态
        assertTrue(channel.finish());

        // 读取消息
        ByteBuf read = channel.readInbound();
        // 验证消息是否有三帧，其中每帧（切片）有3个字节
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        // 数据读完，返回NULL
        assertNull(channel.readInbound());
        buf.release();
    }

}