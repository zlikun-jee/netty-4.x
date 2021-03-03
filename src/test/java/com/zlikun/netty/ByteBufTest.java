package com.zlikun.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ByteBufTest {

    @Test
    void test() {

        ByteBuf buf = Unpooled.copiedBuffer("Hello!", CharsetUtil.UTF_8);

        // 可读取字节数
        assertEquals(6, buf.readableBytes());
        // 可写入字节数
        assertEquals(58, buf.writableBytes());

        // 索引访问
        for (int i = 0; i < buf.capacity(); i++) {
            buf.getByte(i);
        }
        assertEquals('H', buf.getByte(0));

        // 顺序访问，注意顺序访问通过 read* 方法读取，get* 方法不会移动 readerIndex
        assertEquals('H', buf.readByte());
        assertEquals('e', buf.readByte());
        assertEquals('l', buf.readByte());
        assertEquals('l', buf.readByte());
        assertEquals('o', buf.readByte());
        assertEquals('!', buf.readByte());
        // 通过 #isReadable() 方法判断是否可以继续读
        assertFalse(buf.isReadable());
        // 读取完后继续读会抛出索引越界异常
        try {
            buf.readByte();
        } catch (IndexOutOfBoundsException e) {
            assertNotNull(e);
        }

        // 读完部分被称为可丢弃字节
        // 丢弃已读部分空间，将其重置为可写空间
        buf.discardReadBytes();
        assertEquals(64, buf.writableBytes());

        // 通过 #isWritable() 判断是否可写
        assertTrue(buf.isWritable());
        buf.writeChar('A').writeChar('B').writeChar('C');
        assertEquals(58, buf.writableBytes());

        // 与 JDK 中的 ByteBuffer 类似，通过 #clear() 方法可以清空（重置）缓冲区（读/写索引）
        buf.clear();
        assertFalse(buf.isReadable());
        assertTrue(buf.isWritable());
        // 同样实际并未清空数据，通过 #get* 方法依然可以读取
        assertEquals('A', buf.getChar(0));

        // 查找操作
        buf = Unpooled.buffer(8).writeBytes("ABCDEFG".getBytes(StandardCharsets.UTF_8));
        // 简单查找
        assertEquals(1, buf.indexOf(0, buf.capacity() - 1, (byte) 'B'));
        // 复杂查找，这里的值为什么是0？
        assertEquals(0, buf.forEachByte(value -> value == 'B'));

        // 派生缓冲区，通过派生创建的缓冲区只是一个原缓冲区的视图，如果修改会造成原缓冲区被同步修改
        ByteBuf buf2 = buf.duplicate();
        // 副本对象为了一个新对象（但底层数据仍为原数据）
        assertFalse(buf == buf2);
        // 值相等
        assertEquals(buf, buf2);
        // 切片
        ByteBuf buf3 = buf.slice(2, 4);
        assertEquals(4, buf3.capacity());
        assertEquals('C', buf3.getByte(0));

        // 复制缓冲区
        ByteBuf copy = buf.copy();
        // 值仍然相等，但底层数据则是不同对象（内存空间）
        assertEquals(buf, copy);

        copy.setByte(0, 'X');
        assertEquals('X', copy.getByte(0));
        assertEquals('A', buf.getByte(0));

        // 读写操作
        assertEquals('X', copy.readByte());
        assertEquals('Z', copy.writeByte('Z').getByte(copy.readableBytes()));
    }

}