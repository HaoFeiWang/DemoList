package com.whf.demolist.str;

import org.junit.Test;

public class StringAppend {

    @Test
    public void testStringAppend() {

        long start1 = System.currentTimeMillis();
        String one = "";
        for (int i = 0; i < 1000; i++) {
            String str = String.valueOf(i);
            one = one + str;
        }
        long duration1 = System.currentTimeMillis() - start1;
        System.out.println("时间1 = " + duration1);

        long start2 = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            String str = String.valueOf(i);
            stringBuilder.append(str);
        }
        long duration2 = System.currentTimeMillis() - start2;
        System.out.println("时间2 = " + duration2);
    }
}
