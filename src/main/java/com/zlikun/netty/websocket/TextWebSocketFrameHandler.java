package com.zlikun.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    final ChannelGroup group;

    TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 重写 #userEventTriggered() 方法以处理自定义事件
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.err.println(evt);
            // 如果事件被触发表示握手成功，则从该ChannelPipeline中移除HttpRequestHandler，因为不会接收任何HTTP消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 通知所有已经连接的 WebSocket 客户端新的客户端已经连上了
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            // 添加新的 WebSocket Channel 到 ChannelGroup 中，以便它可以接收到所有的消息
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 增加消息引用计数，将将它写到 ChannelGroup 中所有已经连接的客户端
        group.writeAndFlush(msg.retain());
    }
}
