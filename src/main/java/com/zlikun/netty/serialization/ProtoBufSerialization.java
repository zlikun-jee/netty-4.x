package com.zlikun.netty.serialization;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.marshalling.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.marshalling.*;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.time.LocalDate;

@Slf4j
public class ProtoBufSerialization {

    public static void main(String[] args) {

        ProtoBufSerialization pbs = new ProtoBufSerialization();
        pbs.serialize();

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserInfo implements Serializable {
        // private static final long serialVersionUID = 1L;
        private Long userId;
        private String username;
        private LocalDate birthday;
    }

    static class MyMessageLite extends GeneratedMessageV3 {

        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return null;
        }

        @Override
        protected Message.Builder newBuilderForType(BuilderParent builderParent) {
            return null;
        }

        @Override
        public Message.Builder newBuilderForType() {
            return null;
        }

        @Override
        public Message.Builder toBuilder() {
            return null;
        }

        @Override
        public Message getDefaultInstanceForType() {
            return null;
        }
    }

    void serialize() {

        MessageLite lite = null;

        // 分隔帧
        ProtobufVarint32FrameDecoder decoder = new ProtobufVarint32FrameDecoder();
        EmbeddedChannel channel = new EmbeddedChannel(decoder
                , new ProtobufEncoder()
                , new ProtobufDecoder(lite)
                , new SimpleChannelInboundHandler<Serializable>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Serializable msg) throws Exception {
                log.info("Input: {}", msg);
                UserInfo info = (UserInfo) msg;
                ctx.writeAndFlush(info);


            }
        });

        UserInfo info = new UserInfo(10000L, "kevin", LocalDate.of(2019, 9, 1));
//        channel.writeInbound(info);
        channel.writeOutbound(info);
        channel.finish();

    }

}
