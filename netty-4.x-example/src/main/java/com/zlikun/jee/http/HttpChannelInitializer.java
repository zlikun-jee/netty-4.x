package com.zlikun.jee.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author zlikun
 * @date 2019/2/28 19:10
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private boolean ssl;

    public HttpChannelInitializer(boolean ssl) {
        this.ssl = ssl;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        // HTTPS支持
        if (ssl) {
            SSLEngine sslEngine = newSslContext().newEngine(ch.alloc());
            pipeline.addLast("ssl", new SslHandler(sslEngine));
        }

        // 添加请求解码器
        pipeline.addLast("decoder", new HttpRequestDecoder());
        // 添加响应编码器
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // HttpServerCodec是一个CombinedChannelDuplexHandler，包含了上面两个编/解码器
        // pipeline.addLast("codec", new HttpServerCodec());

        // 聚合HTTP消息
        // 将最大的消息大小为 512 KB 的 HttpObjectAggregator 添加到 ChannelPipeline
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));

        // 开启HTTP压缩
        pipeline.addLast("compressor", new HttpContentCompressor());

        // 支持大文件传输
        pipeline.addLast("chunked", new ChunkedWriteHandler());

        // 业务处理Handler（Dispatcher）
        pipeline.addLast("dispatcher", new HttpServerHandler());

    }

    private SslContext newSslContext() throws CertificateException, SSLException {
        // 使用Netty自带的证书工具生成一个数字证书
        SelfSignedCertificate certificate = null;
        try {
            certificate = new SelfSignedCertificate();
            SslContext sslContext = SslContextBuilder
                    .forServer(certificate.certificate(), certificate.privateKey())
                    .clientAuth(ClientAuth.OPTIONAL)
                    .build();
            return sslContext;
        } catch (CertificateException | SSLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
