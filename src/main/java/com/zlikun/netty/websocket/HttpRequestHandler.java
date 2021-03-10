package com.zlikun.netty.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    final String wsUri;
    static final File INDEX;

    // 初始化 index.html 文件对象
    static {
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.uri())) {
            // 增加引用计数，SimpleChannelInboundHandler会自动减少一次，所以这里是一个弥补措施，用于向下传递消息
            ctx.fireChannelRead(request.retain());
            return;
        }
        // 处理 100 Continue 请求以符合 HTTP/1.1 规范
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue(ctx);
            return;
        }
        // 读取 index.html 文件
        RandomAccessFile file = new RandomAccessFile(INDEX, "r");
        // 构造响应对象
        DefaultHttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
        // 添加响应消息头
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_HTML);
        // 处理KeepAlive相关消息头
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        // 将响应写回客户端
        ctx.write(response);
        // 将index.html写回到客户端
        if (ctx.pipeline().get(SslHandler.class) == null) {
            // 利用DefaultFileRegion零拷贝特性来进行内容传输
            ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        } else {
            // SSL跳加密需要在用户态中完成，所以无法实现零拷贝
            ctx.write(new ChunkedNioFile(file.getChannel()));
        }
        // 将响应最后一段写回客户端（空）
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        // 如果不是KeepAlive请求，写操作完成后关闭Channel
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void send100Continue(ChannelHandlerContext ctx) {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("", cause);
        ctx.close();
    }
}
