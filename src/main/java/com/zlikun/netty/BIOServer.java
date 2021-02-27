package com.zlikun.netty;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * BIO模式
 * <p>
 * $ telnet 127.0.0.1 20000
 * Ctrl + ] 快捷键可以呼出发送数据窗口
 * <p>
 * $ send hello
 */
@Slf4j
public class BIOServer {

    public static void main(String[] args) throws IOException {

        // 连接池
        var pool = Executors.newCachedThreadPool();

        // 创建ServerSocket并开始监听指定端口
        var serverSocket = new ServerSocket(20000);
        log.info("ServerSocket开始运行，监听端口：20000");

        while (true) {
            // 等待客户端连接，程序会在此阻塞，直到有一个客户端连接过来
            final var socket = serverSocket.accept();
            log.info("{}:{}", Thread.currentThread().getName(), "连接到一个客户端");

            pool.execute(() -> handle(socket));
        }

    }

    /**
     * 处理连接的具体逻辑
     *
     * @param socket
     */
    private static void handle(Socket socket) {

        try (socket; var inputStream = socket.getInputStream()) {
            while (true) {
                byte[] buf = new byte[128];
                // 从Socket中读取数据，未读到数据时会阻塞住
                int read = inputStream.read(buf);
                if (read != -1) {
                    var message = new String(buf, 0, read);
                    log.info("{}:{}", Thread.currentThread().getName(), message);
                } else {
                    log.info("{}:{}", Thread.currentThread().getName(), "断开连接");
                    break;
                }
            }
        } catch (IOException e) {
            log.error("从Socket中读取数据出错", e);
        }

    }

}
