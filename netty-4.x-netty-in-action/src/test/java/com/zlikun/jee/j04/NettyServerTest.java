package com.zlikun.jee.j04;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 13:39
 */
@Disabled
class NettyServerTest {

    private NettyServer server;

    @Test
    public void oio() throws InterruptedException {
        server = new NettyOioServer();
        server.server(1234);
    }

    @Test
    public void nio() throws InterruptedException {
        server = new NettyNioServer();
        server.server(1234);
    }

    @Test
    public void epoll() throws InterruptedException {
        server = new NettyEpollServer();
        server.server(1234);
    }

    /**
     * 测试未通过（还没搞明白）
     *
     * @throws InterruptedException
     */
    @Test
    public void local() throws InterruptedException {
        server = new NettyLocalServer();
        server.server(1234);
    }

}