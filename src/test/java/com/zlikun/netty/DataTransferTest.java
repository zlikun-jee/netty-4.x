package com.zlikun.netty;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DataTransferTest {

    /**
     * http://ifeve.com/java-nio-channel-to-channel/
     */
    @Test
    void test() {
        try (var fromFileChannel = new RandomAccessFile("./pom.xml", "r").getChannel();
             var toFileChannel = new RandomAccessFile("target/pom.xml", "rw").getChannel()) {

            // 写入数据的初始位置
            long position = 0;
            // 写入数据的数量
            long count = fromFileChannel.size();

            // 将数据从源通道写入目标通道
            toFileChannel.transferFrom(fromFileChannel, position, count);

//            // 功能与transferFrom一致，只是方向相反
//            fromFileChannel.transferTo(position, count, toFileChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
