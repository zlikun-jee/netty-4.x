package com.zlikun.netty;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class BufferTest {

    /**
     * http://ifeve.com/buffers/
     */
    @Test
    void testByteBuffer() {
        // 缓冲区本质上是一个内存块，由 capacity 属性指定其空间大小（不可变），底层数据结构为一个数组
        var buf = ByteBuffer.allocate(128);

        // 容量 capacity 初始化时指定
        assertEquals(128, buf.capacity());
        // 写数据时，position 属性表示当前位置，初始为0，当一个元素写入缓冲区后，该属性值会移动到下一个可写入位置，最大为 capacity - 1
        // 读数据时，position 属性表示当前位置，初始为0，当一个元素读出缓冲区后，该属性值会移动到下一个可读出位置
        assertEquals(0, buf.position());
        // 写数据时，limit 属性表示最多可以向缓冲区中写入多少数据
        // 读数据时，limit 属性等于缓冲区的容量
        assertEquals(128, buf.limit());

        // 单次写入数据
        buf.put((byte) 1);

        // 无论怎样，容量是不会变化的
        assertEquals(128, buf.capacity());
        // 写入一条数据，position 值会后移一位，表示下一个可写入位置
        assertEquals(1, buf.position());
        // 写模式下，limit 值等价于 capacity，表示最大可写元素数量，所以写模式下值不会变化
        assertEquals(128, buf.limit());

        // 批量写入数据，其中 offset 参数和 length 参数为可选参数，表示写入数组中部分数据
        buf.put(new byte[]{1, 2, 3, 4, 5}, 1, 3);

        // 写入三个元素，继续向后移动3次
        assertEquals(4, buf.position());

        // 如果要读取数据，需要先执行 flip() 函数
        buf.flip();

        // 无论怎样，容量是不会变化的
        assertEquals(128, buf.capacity());
        // 切换到读模式后，position 值会被重置为0，表示初始读取位置
        assertEquals(0, buf.position());
        // 切换到读模式后，limit 值会被重置为最大可读取元素位置
        assertEquals(4, buf.limit());

        // 读取一个元素
        assertEquals(1, buf.get());

        // 读模式下，limit 会随着读出元素向后移动，指向下一个可读取位置，最大为全部可读取数量（limit）
        assertEquals(1, buf.position());
        // 读模式下，limit 表示最大可读元素数量，在读取过程中不会发生变化
        assertEquals(4, buf.limit());

        // 读出全部数据
        while (buf.hasRemaining()) {
            buf.get();
        }

        // 此时 position 等价于 limit，注意后面不能再读数据，否则会抛出 java.nio.BufferUnderflowException 异常
        assertEquals(buf.limit(), buf.position());

        // 重新写入元素需要先清空缓冲区，本质上是重置了 position 和 limit 值来实现的模式切换，实际数据依然存在底层数组上
        buf.clear();
        buf.put(new byte[]{1, 2, 3, 4, 5, 6, 7});

        buf.flip();

        buf.get();      // 按顺序读取元素
        assertEquals(1, buf.position());

        buf.get();      // 按顺序读取元素
        assertEquals(2, buf.position());

        // 压缩缓冲区，本质上是将未读取数据向左移动（从0覆盖），这将导致前面读过的数据被覆盖
        // 执行过后缓冲区可以再次写入数据，但会从新位置（remain()）开始
        var r = buf.remaining();
        assertEquals(5, r);
        buf.compact();

        assertEquals(r, buf.position());

        // 通过索引来读取数据会看到未读数据被重置为从0开始，原来读过的数据则被覆盖
        assertEquals(3, buf.get(0));

        // 此时将可以重新写入数据
        buf.put("ABC".getBytes());
        assertEquals(r + 3, buf.position());

        // 清空缓冲区后重新写入
        // 实际上将未真的清空缓冲区，仅仅是重置了 position 和 limit 等属性，原有数据依然可以被读取到，但注意此时写入数据将会覆盖原有数据
        // 前面的 compact() 函数有类似功能（实际效果不同，但都会造成 position 等属性重置，缓冲区由读取状态转换为写入状态）
        buf.clear();
        // 虽然执行了清空操作，但数据其实仍然存在，此时写入数据则会覆盖原有数据
        assertEquals(3, buf.get(0));
        assertEquals('A', buf.get(5));

        // 此时开始写入数据将覆盖原有数据
        buf.put("DEFG".getBytes());
        assertEquals('D', buf.get(0));
        assertEquals('A', buf.get(5));

        // 切换为读取模式，本质上依然是重置 position 和 limit 属性值
        buf.flip();

        assertEquals('D', buf.get());

        // 也可以直接通过修改 position 来改变读取位置
        buf.position(2);
        assertEquals('F', buf.get());
        assertEquals(3, buf.position());

        // 实际底层数组从索引5开始依然存有数据，但由于limit的限制，直接通过遍历的方式是无法读取到的
        buf.clear();
        // 读模式下无法读取，反而写模式下可以，因为读取时会判断索引跟limit值大小
        assertEquals('A', buf.get(5));

        // 另一种绕过的方法是直接读取底层数组
        buf.flip();
        assertEquals('A', buf.array()[5]);

        // mark 属性用于标记元素，通过 reset() 函数可以跳回标记位置
        buf.clear();
        buf.put("ABCDEFGH".getBytes());
        buf.flip();
        assertEquals('A', buf.get());
        assertEquals(1, buf.position());
        buf.mark();
        assertEquals('B', buf.get());
        buf.reset();
        assertEquals('B', buf.get());

        // rewind() 函数与 flip() 函数类似，但注意其不重置 limit 属性，所以使用时要格外注意
    }

    @Test
    void testIntBuffer() {
        // 创建Buffer（指定容量）
        var buf = IntBuffer.allocate(128);

        // 写入数据
        buf.put(1);
        buf.put(2);
        buf.put(new int[]{1, 2, 3, 4, 5}, 2, 3);

        // 翻转，这是由缓冲区类内部实现决定的
        buf.flip();

        // 读取数据
        while (buf.hasRemaining()) {
            log.info("Read: {}", buf.get());
        }

        // 清空Buffer
        buf.clear();

    }

}
