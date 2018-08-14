package com.zlikun.jee.request.impl;

import com.zlikun.jee.request.RequestHandler;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * 这里仅用于演示，实际应用不会把构造Response逻辑放在业务代码中
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 20:52
 */
public class DefaultRequestHandler extends RequestHandler {
    @Override
    public FullHttpResponse handle(FullHttpRequest request) {

        // 构造响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer("工程首页", CharsetUtil.UTF_8));
        // 添加响应消息头
        response.headers().add(CONTENT_TYPE, "text/plain")
                .add(CONTENT_LENGTH, response.content().readableBytes())
                .add(CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        return response;
    }
}
