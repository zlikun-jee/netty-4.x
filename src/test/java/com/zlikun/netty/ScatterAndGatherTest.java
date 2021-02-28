package com.zlikun.netty;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

public class ScatterAndGatherTest {

    FileChannel channel;

    /**
     * http://ifeve.com/java-nio-scattergather/
     */
    @Test
    void testScatter() throws IOException {
        var header = ByteBuffer.allocate(16);
        var body = ByteBuffer.allocate(128);

        var bufferArray = new ByteBuffer[]{header, body};

        // 当一个buffer被写满时接着写下一个buffer，因此不适用于消息大小不固定的场景
        channel.read(bufferArray);
    }

    @Test
    void testGather() throws IOException {
        var header = ByteBuffer.allocate(16);
        var body = ByteBuffer.allocate(128);
        var bufferArray = new ByteBuffer[]{header, body};

        // 读取数据时不存在需要消息固定大小的问题，会依次读完buffer中的数据，写入到channel中
        channel.write(bufferArray);
    }

}
