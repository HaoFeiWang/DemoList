package com.whf.demolist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


/**
 * Created by @author WangHaoFei on 2017/11/13.
 */
@RunWith(Parameterized.class)
public class ParameterUnitTest {

    private int a;
    private int b;
    private int sum;

    @Parameterized.Parameters
    public static Collection setDataList() {
        return Arrays.asList(new Integer[][]{{1, 1, 2}, {2, 2, 4}, {3, 3, 6}});
    }

    public ParameterUnitTest(int a, int b, int sum) {
        this.a = a;
        this.b = b;
        this.sum = sum;
    }

    @Test
    public void add() {
        assertEquals(sum, a + b);
    }
}
