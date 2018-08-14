package com.zlikun.jee.j01;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 1.1 Java网络编程，下面只是一个服务端程序，可以使用telnet来测试
 * $ telnet localhost 1234
 * 输入：xxx
 * 响应：ack: xxx
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 11:30
 */
public class ServerSocketTest {

    @Test
    public void test() throws IOException {

        // 创建一个ServerSocket，监听1234端口
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            // 阻塞，直到有一个连接建立，返回建立的连接套接字
            try (Socket clientSocket = serverSocket.accept()) {
                // 开始处理连接传入信息
                try (
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    String request, response;
                    // 循环读取连接传入信息，当传入"Done"时退出
                    // readLine()用于阻塞读取，直到读取到一个换行符结束（用于接收一行输入，而非一个字符或字节输入）
                    while ((request = reader.readLine()) != null) {
                        if ("Done".equals(request)) {
                            break;
                        }
                        // 处理请求，返回响应信息，通过socket连接写回（服务端发送响应到客户端）
                        response = processRequest(request);
                        writer.println(response);
                    }
                }
            }
        }

    }

    private String processRequest(String request) {
        return "ack: " + request;
    }

}
