package com.zlikun.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

@Slf4j
public class DatagramChannelTest {

    /**
     * http://ifeve.com/datagram-channel/
     */
    @Test
    void testReceive() {
        try (var channel = DatagramChannel.open()) {

            // 监听端口
            channel.socket().bind(new InetSocketAddress(9999));
            log.info("Started ...");

            // 接收数据
            var buf = ByteBuffer.allocate(128);
            channel.receive(buf);
            log.info("Received ...");

            buf.flip();
            log.info(new String(buf.array()));

        } catch (IOException e) {
            log.error("", e);
        }
    }


    @Test
    void testSend() {
        try (var channel = DatagramChannel.open()) {

            var buf = ByteBuffer.wrap("Hello!".getBytes());
            channel.send(buf, new InetSocketAddress("127.0.0.1", 9999));

            log.info("Sent ...");
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
