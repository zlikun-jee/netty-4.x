package com.zlikun.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;

class ContinuationWebSocketFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {

    }
}
