package com.zlikun.jee.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * BIO EchoServer
 *
 * @author zlikun
 * @date 2019/3/4 10:49
 */
public class BIOEchoServer {

    private static volatile boolean exit = false;

    public static void main(String[] args) {
        // 开启一个ServerSocketChannel
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            // 绑定一个本地端口
            server.bind(new InetSocketAddress(1234));
            // 循环接收客户端请求，并进行处理
            while (!exit) {
                // 接收客户端请求，如果没有请求则阻塞在这里
                SocketChannel channel = server.accept();
                // 分配一个缓冲区并读取客户端请求Channel中的数据
                ByteBuffer buffer = ByteBuffer.allocate(64);
                channel.read(buffer);
                // 处理客户端请求Channel的数据
                exit = doRequest(buffer);
                // 往客户端channel中写入数据（响应）
                doResponse(channel, buffer);
            }
            System.out.println("Shutdown ...");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理响应，将请求消息原样写回并关闭连接
     *
     * @param channel
     * @param buffer
     */
    private static void doResponse(SocketChannel channel, ByteBuffer buffer) {
        try {
            channel.write(buffer);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理请求，当消息为"shutdown"时返回true，表示退出
     *
     * @param buffer
     * @return
     */
    private static boolean doRequest(ByteBuffer buffer) {
        // 切换buffer为读取模式
        buffer.flip();
        // 打印缓冲区中消息
        String message = new String(buffer.array(), StandardCharsets.UTF_8);
        System.out.println("Receive: " + message);
        return message.trim().equalsIgnoreCase("shutdown");
    }

}
