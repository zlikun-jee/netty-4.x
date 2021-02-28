package com.zlikun.netty;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.channels.SelectionKey.OP_READ;

/**
 * 聊天客户端
 */
@Slf4j
public class IMClient {

    public static void main(String[] args) throws InterruptedException {

        final var service = Executors.newSingleThreadScheduledExecutor();

        try (var selector = Selector.open()) {
            var sc = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6666));
            sc.configureBlocking(false);
            sc.register(selector, OP_READ);
            log.info("客户端 {} is ready.", sc.getLocalAddress());

            // 定时接收消息
            service.scheduleAtFixedRate(() -> {
                try {
                    receive(selector);
                } catch (IOException e) {
                    log.error("", e);
                }
            }, 0, 100, TimeUnit.MILLISECONDS);

            // 接收标准输入发送群消息
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String s = scanner.nextLine();
                if ("quit".equals(s)) break;
                send(sc, s);
            }

            System.exit(0);
        } catch (IOException e) {
            log.error("", e);
        }

    }

    /**
     * 发送消息，返回发送字节数
     *
     * @param sc
     * @param message
     * @return
     * @throws IOException
     */
    private static int send(SocketChannel sc, String message) throws IOException {
        return sc.write(ByteBuffer.wrap(message.getBytes()));
    }

    /**
     * 接收消息
     *
     * @param selector
     * @throws IOException
     */
    private static void receive(Selector selector) throws IOException {
        if (selector.select() == 0) return;
        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            if (key.isReadable()) {
                var sc = (SocketChannel) key.channel();
                var buffer = ByteBuffer.allocate(128);
                var len = sc.read(buffer);
                log.info(new String(buffer.array(), 0, len));
            }
            keys.remove();
        }
    }

}
