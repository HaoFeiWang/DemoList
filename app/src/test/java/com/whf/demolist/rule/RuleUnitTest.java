package com.whf.demolist.rule;

import org.junit.Rule;
import org.junit.Test;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class RuleUnitTest {

    @Rule
    public CustomRule customRule = new CustomRule();

    @Test
    public void add(){
        System.out.println("测试中...");
    }
}
