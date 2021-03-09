package com.zlikun.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

public class HttpChannelInitializer extends ChannelInitializer<Channel> {

    boolean client;
    SslContext sslContext;

    public HttpChannelInitializer(boolean client, SslContext sslContext) {
        this.client = client;
        this.sslContext = sslContext;
    }

    public HttpChannelInitializer(boolean client) {
        this(client, null);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 心跳检测（不关注消息是否加密，所以可以放在SSL前面），每60秒触发Idle状态，
        // 后面心跳Handler监听到该事件时发送一条心跳消息，如果发送失败则关闭连接
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
                .addLast("heartbeat", new HeartbeatHandler());

        // 设置SSL/TLS，通常放在最前面（对消息内容不敏感的除外）
        if (sslContext != null) {
            SSLEngine engine = sslContext.newEngine(ch.alloc());
            pipeline.addLast("ssl", new SslHandler(engine));
        }
        // 设置编解码器
        if (client) {
            // 如果是客户端程序，编码请求，解码响应
            // pipeline.addLast("decoder", new HttpResponseDecoder());
            // pipeline.addLast("encoder", new HttpRequestEncoder());
            // 与上面两项等价（组合双工Handler），参考：CombinedChannelDuplexHandler
            pipeline.addLast("codec", new HttpClientCodec());
            // HTTP解压缩
            pipeline.addLast("decompressor", new HttpContentDecompressor());
        } else {
            // 如果是服务端程序，解码请求，编码响应
            // pipeline.addLast("decoder", new HttpRequestDecoder());
            // pipeline.addLast("encoder", new HttpResponseEncoder());
            // 与上面两项等价（组合双工Handler），参考：CombinedChannelDuplexHandler
            pipeline.addLast("codec", new HttpServerCodec());
            // HTTP压缩
            pipeline.addLast("compressor", new HttpContentCompressor());
        }
        // 聚合HTTP消息片段，默认HTTP消息是由：请求头、请求内容1、请求内容2、...、最后一块请求内容构成
        // 实际应用中应将其组合为 FullHttpRequest 和 FullHttpResponse 来使用，所以就需要用到如下聚合器
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));

    }
}
