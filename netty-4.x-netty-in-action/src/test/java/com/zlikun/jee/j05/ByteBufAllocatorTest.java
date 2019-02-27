package com.zlikun.jee.j05;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 * 5.5 ByteBuf分配
 *
 * @author zlikun
 * @date 2019/2/27 20:16
 */
public class ByteBufAllocatorTest {

    /**
     * 使用 ByteBufAllocator 接口实现 ByteBuf 池化
     */
    @Test
    public void test() {

        ByteBufAllocator allocator = new PooledByteBufAllocator();
        ByteBuf buf = allocator.buffer(4);
        buf.writeInt(120);
        assertEquals(4, buf.readableBytes());
        assertEquals(120, buf.readInt());

        ByteBuf heapBuf = allocator.heapBuffer(4);
        ByteBuf directBuf = allocator.directBuffer(8);
        CompositeByteBuf compositeBuf = allocator.compositeBuffer(heapBuf.capacity() + directBuf.capacity());
        compositeBuf.addComponents(heapBuf, directBuf);

//        heapBuf.writeInt(12);
//        directBuf.writeInt(3);
//        directBuf.writeInt(4);

        compositeBuf.writeInt(12);
        compositeBuf.writeInt(3);
        compositeBuf.writeInt(4);

        assertEquals(12, compositeBuf.readInt());
        assertEquals(3, compositeBuf.readInt());
        assertEquals(4, compositeBuf.readInt());

    }

}
