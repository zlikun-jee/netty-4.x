package com.zlikun.netty;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappedByteBufferTest {

    /**
     * MappedByteBuffer 直接在内存中操作文件（堆外内存，零拷贝）
     */
    @Test
    void test() throws IOException {

        // 打开文件通道
        RandomAccessFile rw = new RandomAccessFile("./pom.xml", "rw");
        FileChannel channel = rw.getChannel();

        // 通过通道来读写文件
        // 参数一：表示读写模式
        // 参数二：表示映射的起始位置
        // 参数三：表示映射的长度
        // 只能对 position 和 size 参数范围内的数据
        MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_WRITE, 0, 16);
        // 将第三个元素修改为 'X'
        mbb.put(2, (byte) 'X');
        mbb.force();

        assertEquals('X', mbb.get(2));

        mbb.put(2, (byte) 'x');
        mbb.force();

        channel.close();
        rw.close();
    }

}
