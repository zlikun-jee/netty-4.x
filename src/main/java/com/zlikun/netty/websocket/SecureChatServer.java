package com.zlikun.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

@Slf4j
public class SecureChatServer {


    public static void main(String[] args) throws CertificateException, SSLException {

        // 自签名证书，仅用于测试（不安全）
        SelfSignedCertificate certificate = new SelfSignedCertificate();
        SslContext context = SslContextBuilder
                .forServer(certificate.certificate(), certificate.privateKey())
                // 生产环境不能用，信任所有证书
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        SecureChatServer endpoint = new SecureChatServer(context);
        ChannelFuture future = endpoint.start(new InetSocketAddress(7777));
        log.info("Server is running.");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> endpoint.destroy()));
        future.channel().closeFuture().syncUninterruptibly();

    }

    ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    EventLoopGroup group = new NioEventLoopGroup();
    Channel channel;
    SslContext sslContext;

    SecureChatServer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    ChannelFuture start(InetSocketAddress address) throws CertificateException, SSLException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer());
        ChannelFuture future = bootstrap.bind(address).syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    ChannelHandler createInitializer() throws CertificateException, SSLException {
        return new SecureWebSocketInitializer(channelGroup, sslContext);
    }

    void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
    }

}
