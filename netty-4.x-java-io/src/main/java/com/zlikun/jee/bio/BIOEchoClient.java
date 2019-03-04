package com.zlikun.jee.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * BIO EchoClient
 *
 * @author zlikun
 * @date 2019/3/4 11:15
 */
public class BIOEchoClient {

    public static void main(String[] args) {

        final String host = "10.10.10.42";
        final int port = 1234;
        try (SocketChannel channel = SocketChannel.open()) {
            channel.connect(new InetSocketAddress(host, port));
            // 发送客户端请求
            doRequest(channel, "shutdown");
            // 处理服务端响应
            doResponse(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void doResponse(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        channel.read(buffer);
        buffer.flip();
        System.out.println("Receive: " + new String(buffer.array(), StandardCharsets.UTF_8));
    }

    private static void doRequest(SocketChannel channel, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        channel.write(buffer);
    }

}
