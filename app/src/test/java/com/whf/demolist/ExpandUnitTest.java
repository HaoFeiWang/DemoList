package com.whf.demolist;

import org.junit.Test;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class ExpandUnitTest {

    @Test(expected = ArithmeticException.class)
    public void division() {
        int i = 5 / 0;
    }

    @Test(timeout = 1000)
    public void timeOut() {
        try {
            System.out.println("开始执行 = " + System.currentTimeMillis());
            Thread.sleep(500);
            System.out.println("开始结束 = " + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
