package com.zlikun.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

@Slf4j
public class SelectorTest {

    /**
     * http://ifeve.com/selectors/
     */
    @Test
    void testSelector() {

        // 打开Selector
        // 注册Channel到Selector，可以注册多个Channel到同一个Selector
        // 注意：与Selector一起使用的Channel必须处在非阻塞模式下，而FileChannel不能切换到非阻塞模式，所以不能配合Selector一起用
        try (var selector = Selector.open();
             var channel = ServerSocketChannel.open();) {

            // 将Channel切换到非阻塞模式
            channel.configureBlocking(false);
            // 绑定监听端口
            channel.bind(new InetSocketAddress(8000));
            log.info("Server is running, listen 8080.");
            // Channel注意到Selector，指定监听事件，多个事件用 按位或 联接，共有四个
            // SelectionKey.OP_CONNECT
            // SelectionKey.OP_ACCEPT
            // SelectionKey.OP_READ
            // SelectionKey.OP_WRITE
            channel.register(selector, OP_ACCEPT);
            while (true) {
                if (selector.select() == 0) continue;
                var keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    var key = keys.next();
                    if (key.isValid()) {
                        if (key.isAcceptable()) {
                            doAccept(selector, key);
                        } else if (key.isConnectable()) {

                        } else if (key.isReadable()) {
                            doRead(key);
                        } else if (key.isWritable()) {
                            doWrite(key);
                        }
                    }
                    keys.remove();
                }
            }

        } catch (IOException e) {
            log.error("打开Selector出错", e);
        }

    }

    void doAccept(Selector selector, SelectionKey key) {
        var channel = (ServerSocketChannel) key.channel();
        try {
            // 获取客户端连接
            var sc = channel.accept();
            sc.configureBlocking(false);
            // 注册读取事件（读取客户端向服务端发送的数据）
            sc.register(selector, OP_READ);
        } catch (IOException e) {
            log.error("", e);
        }

    }

    void doRead(SelectionKey key) {
        var buf = ByteBuffer.allocate(128);
        var channel = (SocketChannel) key.channel();
        try {
            var len = channel.read(buf);
            if (len == -1) channel.close();
            else log.info(new String(buf.array(), 0, len));
        } catch (IOException e) {
            log.error("", e);
        }
    }

    void doWrite(SelectionKey key) {
        var buf = ByteBuffer.wrap("OK".getBytes());
        var channel = (SocketChannel) key.channel();
        try {
            if (channel.isOpen()) {
                channel.write(buf);
            }
            if (!buf.hasRemaining()) {
                key.interestOps(OP_READ);
            }
            buf.compact();
        } catch (IOException e) {
            log.error("", e);
        }
    }

}
