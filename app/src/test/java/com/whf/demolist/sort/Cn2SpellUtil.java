package com.whf.demolist.sort;

import android.text.TextUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Created by ouyangfan on 2016/4/20.
 * chinese convert to spell util class
 */
public class Cn2SpellUtil {

    /**
     * chinese convert to spell first letter,
     * others do not convert
     *
     * @param chinese chinese
     * @return first uppercase letter spell
     */
    public static String convertToFirstUpperSpell(String chinese) {
//        if (TextUtils.isEmpty(chinese) || TextUtils.isEmpty(chinese.trim())) {
//            return "[";
//        }

        return CnToSpell.getInstance().getFirstCharacter(chinese.trim());
    }

    @Test
    public void testSort(){
//        Compare[] names = new Compare[]{new Compare("dm"),new Compare("cd"),new Compare("ab"),
//                new Compare("Aa"),new Compare("赖赖"),new Compare("Cc"),new Compare("伶俐"),
//                new Compare("戴戴"),new Compare("慈慈"),new Compare("伯伯"),new Compare("妈妈"),
//                new Compare("阿姨"),new Compare("Bb"),new Compare("LI"),new Compare("Ww"),
//                new Compare("文明"),new Compare("岑岑"()),"bB","1","2","3","9","Bayi1"};

        List<String> srcString = new ArrayList<>();
        srcString.add("cd");
        srcString.add("ab");
        srcString.add("Aa");
        srcString.add("Cc");
        srcString.add("文明");
        srcString.add("岑岑");
        srcString.add("bB");
        srcString.add("Bayi1");
        srcString.add("伯伯");
        srcString.add("123");
        srcString.add("888");
        srcString.add("9654");
        srcString.add("赖赖");
        srcString.add("妈妈");
        srcString.add("aA");
        srcString.add("阿姨");
        srcString.add("Bb");
        srcString.add("Cc");
        srcString.add("cd");
        srcString.add("慈慈");
        srcString.add("Dd");
        srcString.add("dm");
        srcString.add("戴戴");
        srcString.add("LI");
        srcString.add("伶俐");
        srcString.add("Ww");

        Collections.sort(srcString,new CompatorTest());
        System.out.println(srcString);
    }





}
