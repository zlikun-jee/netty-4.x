package com.zlikun.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketInitializer extends ChannelInitializer<Channel> {

    ChannelGroup group;

    public WebSocketInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 设置WebSocket相关Handler
        pipeline.addLast(
                // HTTP请求/响应编码
                new HttpServerCodec()
                // 写入一个文件的内容
                , new ChunkedWriteHandler()
                // 消息聚合，合并为FullHttpRequest和FullHttpResponse实例
                , new HttpObjectAggregator(512 * 1024)
                // 处理FullHttpRequest（不发送到/ws URI的请求）
                , new HttpRequestHandler("/ws")
                , new WebSocketServerProtocolHandler("/ws")
                , new TextWebSocketFrameHandler(this.group)
//                , new BinaryWebSocketFrameHandler()
//                , new ContinuationWebSocketFrameHandler()
        );

    }

}
