package com.zlikun.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

@Slf4j
public class PipeTest {

    /**
     * http://ifeve.com/pipe/
     */
    @Test
    void test() throws IOException {
        var pipe = Pipe.open();
        write(pipe, "Hello Netty!");
        read(pipe);
    }

    /**
     * 向管道写入数据
     *
     * @param pipe
     * @param message
     * @throws IOException
     */
    void write(Pipe pipe, String message) throws IOException {
        // 获取Sink通道，向管道写入数据
        var sinkChannel = pipe.sink();

        var buf = ByteBuffer.wrap(message.getBytes());
        while (buf.hasRemaining())
            sinkChannel.write(buf);

        log.info("Sent ...");
    }

    /**
     * 从管道读取数据
     *
     * @param pipe
     * @throws IOException
     */
    void read(Pipe pipe) throws IOException {
        // 获取Source通道，从管道读取数据
        var sourceChannel = pipe.source();
        var buf = ByteBuffer.allocate(128);
        int len = sourceChannel.read(buf);

        log.info("Received ...");
        log.info(new String(buf.array(), 0, len));
    }

}
