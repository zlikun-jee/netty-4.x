package com.zlikun.netty;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 与主题无关的测试
 */
public class OtherTest {

    /**
     * 位运算
     */
    @Test
    void testBitOperation() {
        // 按位或，1按位或任意值为1，否则为0
        assertEquals(0b0110, 0b0100 | 0b0010);
        assertEquals(0b0110, 0b0110 | 0b0010);
        assertEquals(0b0111, 0b0100 | 0b0011);

        // 按位与，1按位与1为1，否则为0
        assertEquals(0b0000, 0b0100 & 0b0010);
        assertEquals(0b0010, 0b0110 & 0b0010);

        // 异或，相应位不同则为1，同则为0，同数异或等于0
        assertEquals(0b0110, 0b0100 ^ 0b0010);
        assertEquals(0b0100, 0b0110 ^ 0b0010);
        assertEquals(0b0010, 0b0000 ^ 0b0010);
        assertEquals(0b0000, 0b1011 ^ 0b1011);

        // 左移
        assertEquals(0b0001, 1 << 0);
        assertEquals(0b0010, 1 << 1);
        assertEquals(0b0100, 1 << 2);

        // 右移
        assertEquals(0b0001, 0b0001 >> 0);
        assertEquals(0b0001, 0b0010 >> 1);
        assertEquals(0b0001, 0b0011 >> 1);
    }

}
