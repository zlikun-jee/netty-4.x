package com.zlikun.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

@Slf4j
public class ChannelTest {

    /**
     * http://ifeve.com/channels/
     */
    @Test
    void testFileChannel() {

        // 通道从文件中读取数据，写入缓冲区，再从缓冲区中读出来
        try (var file = new RandomAccessFile("./pom.xml", "rw");
             var channel = file.getChannel()) {
            var buf = ByteBuffer.allocate(128);
            var read = channel.read(buf);
            while (read != -1) {
                buf.flip();
                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get());
                }
                buf.clear();
                read = channel.read(buf);
            }
        } catch (IOException e) {
            log.error("读取文件出错", e);
        }


    }

}
