package com.zlikun.netty;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static java.nio.channels.SelectionKey.*;

/**
 * 在线群聊系统
 * 1. 多人群聊
 * 2. 监听上线离线
 * 3. 消息转发
 */
@Slf4j
public class IMServer {

    public static void main(String[] args) {
        try (Selector selector = Selector.open(); ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 设置为非阻塞模式
            ssc.configureBlocking(false);
            // 监听端口
            ssc.bind(new InetSocketAddress(6666));

            log.info("Server started .");

            // 注册到Selector上
            ssc.register(selector, OP_ACCEPT);

            // 事件循环
            while (true) {
                if (selector.select() == 0) continue;
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    if (key.isValid()) {
                        if (key.isAcceptable()) {
                            doAccept(selector, key);
                        }
//                        if (key.isConnectable()) {
//                            log.info("忽略 ...");
//                        }
                        if (key.isReadable()) {
                            doRead(selector, key);
                        }
//                        if (key.isWritable()) {
//                            doWrite(key);
//                        }
                    }
                    keys.remove();
                }
            }

        } catch (IOException e) {
            log.error("", e);
        }
    }

//    /**
//     * 处理写事件
//     *
//     * @param key
//     */
//    private static void doWrite(SelectionKey key) {
////        log.info("#doWrite 未实现");
//    }

    /**
     * 处理读事件
     *
     * @param selector
     * @param key
     */
    private static void doRead(Selector selector, SelectionKey key) {
        var sc = (SocketChannel) key.channel();
        var buffer = ByteBuffer.allocate(128);
        try {
            // 从管道中读取数据
            var len = sc.read(buffer);
            if (len == 0) return;
            // 服务端记录消息
            var username = sc.getRemoteAddress().toString();
            var message = new String(buffer.array(), 0, len);
            log.info("{} > {}", username, message);
            // 转发消息给其它客户端（排除自身）
            for (var key2 : selector.keys()) {
                var c = key2.channel();
                // 排除自身
                if (c instanceof SocketChannel && c != sc) {
                    // 将数据写入到通道
                    var s = String.format("%s > %s", username, message);
                    ((SocketChannel) c).write(ByteBuffer.wrap(s.getBytes()));
                }
            }
        } catch (IOException e) {
            log.error("读写数据出错", e);
            try {
                // 提示离线
                log.info("{} 已下线 ...", sc.getRemoteAddress());
                // 取消注册
                key.cancel();
                // 关闭通道
                sc.close();
            } catch (IOException e2) {
                log.error("用户下线操作出错", e2);
            }
        }
    }

    /**
     * 处理客户端连接事件
     *
     * @param selector
     * @param key
     */
    private static void doAccept(Selector selector, SelectionKey key) {
        var channel = (ServerSocketChannel) key.channel();
        try {
            var sc = channel.accept();
            sc.configureBlocking(false);
            sc.register(selector, OP_READ | OP_WRITE);

            log.info("客户端 {} 已上线 ...", sc.getRemoteAddress());
        } catch (IOException e) {
            log.error("", e);
        }
    }

}
