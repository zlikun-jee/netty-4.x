package com.zlikun.jee.request;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 应在该类中将请求对象拆解开，抽出uri、method、headers、content、parameters等信息
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2018/8/14 20:50
 */
public abstract class RequestHandler {

    public abstract FullHttpResponse handle(FullHttpRequest request);

}
