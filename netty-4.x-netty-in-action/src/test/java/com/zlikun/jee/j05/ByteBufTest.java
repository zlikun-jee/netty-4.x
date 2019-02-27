package com.zlikun.jee.j05;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 5.3 字节级操作
 *
 * @author zlikun
 * @date 2019/2/27 19:48
 */
public class ByteBufTest {

    /**
     * 5.3.1 随机访问索引
     */
    @Test
    public void ch01() {
        ByteBuf buf = Unpooled.copiedBuffer("Hello!", Charset.defaultCharset());
        // 与Java普通数组一样，ByteBuf的索引也是从零开始[0, buf.capacity() - 1]
        for (int i = 0; i < buf.capacity(); i++) {
            byte b = buf.getByte(i);
            System.out.println((char) b);
        }
    }

    /**
     * 5.3.2 顺序访问索引
     */
    @Test
    public void ch02() {

    }

    /**
     * 5.3.3 可丢弃字节
     */
    @Test
    public void ch03() {
        ByteBuf buf = Unpooled.copiedBuffer("Hello!", Charset.defaultCharset());
        buf.readByte();
        buf.readByte();
        // read操作会移动readerIndex，此时移动了两位
        assertEquals("llo!", buf.discardReadBytes().toString(Charset.defaultCharset()));
    }

    /**
     * 5.3.4 可读字节
     */
    @Test
    public void ch04() {

    }

    /**
     * 5.3.5 可写字节
     */
    @Test
    public void ch05() {
        ByteBuf buf = Unpooled.buffer(4);
        assertEquals(4, buf.writableBytes());
        // #writableBytes()用来判断缓冲区中是否还有足够空间
        // 一个整型4个字节，所以刚好写满
        while (buf.writableBytes() >= 4) {
            buf.writeInt(120);
        }

        assertEquals(120, buf.readInt());
    }

    /**
     * 5.3.6 索引管理
     */
    @Test
    public void ch06() {

    }

    /**
     * 5.3.7 查找操作
     */
    @Test
    public void ch07() {

    }

    /**
     * 5.3.8 派生缓冲区
     */
    @Test
    public void ch08() {

    }

    /**
     * 5.3.9 读/写操作
     */
    @Test
    public void ch09() {

    }

}
