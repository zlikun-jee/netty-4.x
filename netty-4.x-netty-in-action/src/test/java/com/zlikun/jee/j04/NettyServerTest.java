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

}