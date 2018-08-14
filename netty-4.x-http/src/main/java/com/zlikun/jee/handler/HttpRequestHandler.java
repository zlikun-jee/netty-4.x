package com.zlikun.jee.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 20:03
 */
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * 打印请求对象中包含的信息（调试用）
     * $ curl localhost
     * $ curl 'localhost/api/v3/user?id=1024&t=1534250178480'
     * $ curl -X POST --data 'id=1024&name=zlikun' 'localhost/api/v3/user'
     *
     * @param request
     */
    private void printRequest(FullHttpRequest request) {
        // uri = /
        // uri = /api/v3/user?id=1024&t=1534250178480
        // uri = /api/v3/user
        System.out.printf("uri = %s%n", request.uri());
        // protocolVersion = HTTP/1.1
        System.out.printf("protocolVersion = %s%n", request.protocolVersion());
        // method = GET
        System.out.printf("method = %s%n", request.method());
        // refCnt = 1
        System.out.printf("refCnt = %d%n", request.refCnt());

        // POST请求时，content才有值
        // content = id=1024&name=zlikun
        ByteBuf content = request.content();
        if (content != null) {
            System.out.printf("content = %s%n", content.toString(CharsetUtil.UTF_8));
        }
        // decoderResult = success
        DecoderResult decoderResult = request.decoderResult();
        System.out.printf("decoderResult = %s%n", decoderResult);

        // Host=localhost
        // User-Agent=curl/7.56.1
        // Accept=*/*
        // content-length=0
        System.out.println("--headers--");
        HttpHeaders headers = request.headers();
        headers.forEach(System.out::println);

        // 没有输出，trailingHeaders是啥意思？
        System.out.println("--trailingHeaders--");
        HttpHeaders trailingHeaders = request.trailingHeaders();
        trailingHeaders.forEach(System.out::println);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        // 打印请求信息
        printRequest(msg);

        // 构造响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer("Welcome to Netty !", CharsetUtil.UTF_8));
        // 添加响应消息头
        response.headers().add(CONTENT_TYPE, "text/plain")
                .add(CONTENT_LENGTH, response.content().readableBytes())
                .add(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        // 向客户端写回响应
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
