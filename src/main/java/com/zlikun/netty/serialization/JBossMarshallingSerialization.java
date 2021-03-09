package com.zlikun.netty.serialization;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.marshalling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import java.io.Serializable;
import java.time.LocalDate;

@Slf4j
public class JBossMarshallingSerialization {

    public static void main(String[] args) {
        JBossMarshallingSerialization jms = new JBossMarshallingSerialization();
        jms.marshall();

//        UserInfo info = new UserInfo(10000L, "kevin", LocalDate.of(2019, 9, 1));
//
//        try {
//            MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
//            MarshallingConfiguration config = new MarshallingConfiguration();
//            config.setVersion(5);
//            Marshaller marshaller = factory.createMarshaller(config);
////            Unmarshaller unmarshaller = factory.createUnmarshaller(config);
//
//            ByteBuffer buf = ByteBuffer.allocate(1024);
//            ByteOutput output = Marshalling.createByteOutput(buf);
//            marshaller.start(output);
//            marshaller.writeObject(info);
////            buf.flip();
////            byte[] data = buf.array();
////            log.info("byte array length: {}", data.length);
//
//            output.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

    void marshall() {
        MarshallerProvider marshallerProvider = createMarshallerProvider();
        MarshallingEncoder encoder = new MarshallingEncoder(marshallerProvider);
        MarshallingDecoder decoder = new MarshallingDecoder(createUnmarshallerProvider(), 1024);

        EmbeddedChannel channel = new EmbeddedChannel(decoder, encoder, new SimpleChannelInboundHandler<Serializable>() {
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

    UnmarshallerProvider createUnmarshallerProvider() {
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration config = new MarshallingConfiguration();
        config.setVersion(5);

        return new DefaultUnmarshallerProvider(factory, config);
    }

    MarshallerProvider createMarshallerProvider() {
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration config = new MarshallingConfiguration();
        config.setVersion(5);

        return new DefaultMarshallerProvider(factory, config);
    }

}
