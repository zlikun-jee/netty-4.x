package com.zlikun.jee.j05;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

/**
 * 5.2.2 ByteBuf的使用模式
 *
 * @author zlikun
 * @date 2019/2/27 19:29
 */
public class ByteBufModeTest {

    /**
     * 堆缓冲区，将数据存储在JVM的堆空间中，这种被称为支撑数组，能在没有使用池化的情况下快速的分配和释放
     */
    @Test
    public void heap() {
        ByteBuf buf = null;
        // 检查ByteBuf是否有一个支撑数组
        if (buf.hasArray()) {
            // 如果有，则获取对该数组的引用
            byte[] array = buf.array();
            // 计算第一个字节的偏移量
            int offset = buf.arrayOffset() + buf.readerIndex();
            // 获得可读字节数
            int length = buf.readableBytes();
            // 使用数组、偏移量和长度作为参数调用你的方法
            handleArray(array, offset, length);
        }
    }

    private void handleArray(byte[] array, int offset, int length) {
        // ...
    }

    /**
     * 直接缓冲区的内容将驻留在常规的会被垃圾回收的堆之外
     */
    @Test
    public void direct() {
        ByteBuf buf = null;
        // 检查ByteBuf是否由数组支撑，不是则是一个直接缓冲区
        if (!buf.hasArray()) {
            // 获取可读字节数
            int length = buf.readableBytes();
            // 分配一新的数组来保存具该长度字节数据
            byte[] array = new byte[length];
            // 将字节复制到该数组
            buf.getBytes(buf.readerIndex(), array);
            // 使用数组、偏移量和长度作为参数调用你的方法
            handleArray(array, 0, length);
        }
    }

    /**
     * 复合缓冲区，为多个ByteBuf提供一个聚合视图
     */
    @Test
    public void composite() {
        CompositeByteBuf buf = Unpooled.compositeBuffer();
        ByteBuf header = null;  // can be backing or direct
        ByteBuf body = null;    // can be backing or direct
        // 将ByteBuf实例追加到CompositeByteBuf
        buf.addComponents(header, body);
        // 删除第一个组件的ByteBuf
        buf.removeComponent(0);
        // 循环遍历所有的ByteBuf实例
        for (ByteBuf b : buf) {
            System.out.println(b.toString());
        }

    }

}
