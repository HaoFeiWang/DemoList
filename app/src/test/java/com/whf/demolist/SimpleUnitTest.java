package com.whf.demolist;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Junit单元测试
 * Created by @author WangHaoFei on 2017/11/13.
 */
public class SimpleUnitTest {
    public static final String TAG = "JUnitTest：";

    private static final Integer A = 1;
    private static final Integer B = 2;
    private static final Integer C = A;
    private static final Integer D = 1;

    private static Integer E = null;

    @Test
    public void onResumeBefore(){
        System.out.println(TAG + "onResumeBefore");
//        System.out.println(Thread.currentThread().getName());
    }

    @Test
    public void onResume() {
        System.out.println(TAG + "onResume");
//        System.out.println(Thread.currentThread().getName());

        assertEquals("2+2 = 4", 4, 2 + 2);
        assertNotEquals("2+2 != 5", 5, 2 + 2);

        assertTrue("A == D", A.equals(D));
        assertFalse("A != B", A.equals(B));

        assertNull("E == null", E);
        assertNotNull("A != null", A);

        assertSame("A和C是同一个对象", A, C);
        assertNotSame("A和B不是同一个对象", A, B);

        assertArrayEquals("两个数组相等", new int[]{1, 2, 3}, new int[]{1, 2, 3});
    }

    @Ignore
    @Test
    public void onResumeAfter(){
        System.out.println(TAG + "onResumeAfter");
//        System.out.println(Thread.currentThread().getName());
    }

    @Before
    public void onStart() {
        System.out.println(TAG + "onStart");
    }

    @BeforeClass
    public static void onCreate() {
        System.out.println(TAG + "onCreate");
        System.out.println();
    }

    @After
    public void onPause() {
        System.out.println(TAG + "onPause");
        System.out.println();
    }

    @AfterClass
    public static void onDestory() {
        System.out.println(TAG + "onDestory");
    }
}