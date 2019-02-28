package com.zlikun.jee.http;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

/**
 * @author zlikun
 * @date 2019/2/28 19:20
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            // 打印请求方法及URI信息
            // Method = GET, Uri = /
            System.out.printf("Method = %s, Uri = %s%n", request.method(), request.uri());

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            // Body，输出请求响应
            response.content().writeBytes("Hello!".getBytes());
            // Header: Content-Type
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
            // Header: Content-Length
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

            // 是否使用长连接
            if (HttpUtil.isKeepAlive(request)) {
                // 添加长连接响应消息头
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.writeAndFlush(response);
            } else {
                // 非长连接则在输出响应后关闭连接
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        }

    }

}
