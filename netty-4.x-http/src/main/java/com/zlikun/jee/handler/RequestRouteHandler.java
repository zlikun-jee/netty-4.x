package com.zlikun.jee.handler;

import com.zlikun.jee.request.RequestHandler;
import com.zlikun.jee.route.Router;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 实现请求路由功能
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 20:03
 */
@ChannelHandler.Sharable
public class RequestRouteHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * 自定义请求路由
     */
    private Router router = new Router();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        // 根据uri进行路由
        String uri = request.uri();
        RequestHandler handler = router.route(uri);

        // 使用路由的处理器来处理请求
        FullHttpResponse response = handler.handle(request);

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
