package com.whf.demolist.sort;

import java.util.Arrays;

public class CnToSpell {

    //岑：15053457
    //搭：15110317
    private static CnToSpell instance = new CnToSpell();

    //字母Z使用了两个标签，这里有27个值
    //i, u, v都不做声母, 跟随前面的字母
    //存在问题，需要改进
    private char[] chartable = {
            '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈',
            '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然',
            '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', '座'
    };

    private char[] alphatable = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private int[] table = new int[27];

    public static CnToSpell getInstance() {
        return instance;
    }

    private CnToSpell() {
        for (int i = 0; i < 27; ++i) {
            table[i] = gbValue(chartable[i]);
        }
        System.out.println("table = "+ Arrays.toString(table));
    }

    //根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串
    public String getFirstCharacter(String sourceStr) {
        String result = "";
        try {
            result += charAlpha(sourceStr.charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result.isEmpty()) {
            result = "[";
        }
        return result;
    }

    //主函数,输入字符,得到他的声母,
    //英文字母返回对应的小写字母
    //其他非简体汉字返回 '0'
    private char charAlpha(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return Character.toUpperCase(ch);
        }
        if (ch >= 'A' && ch <= 'Z'
                || ch >= '0' && ch <= '9') {
            return ch;
        }

        if (ch == '岑'){
            return 'C';
        }

        int gb = gbValue(ch);
        System.out.println("get value " + ch + " " + gb);
        if (gb < table[0])
            return '[';
        int i;
        for (i = 0; i < 26; ++i) {
            if (match(i, gb))
                break;
        }
        if (i >= 26)
            return '[';
        else
            return alphatable[i];
    }


    private boolean match(int i, int gb) {
        //正常情况下只可能大于或者等于
        if (gb < table[i]) {
            //正常不该进入
            return false;
        }
        int j = i + 1;

        //字母Z使用了两个标签
        //为了跳过连个连续相同的汉字
        while (j < 26 && (table[j] == table[i])) {
            ++j;
        }
        if (j == 26)
            return gb <= table[j];//如果是则为Z
        else
            return gb < table[j];//如果是则应该是i，如果不是那就返回false继续循环
    }

    //取出汉字的编码
    private int gbValue(char ch) {
        String str = ch + "";
        try {
            byte[] bytes = str.getBytes("GB2312");
            if (bytes.length < 2)
                return 0;
            return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
        } catch (Exception e) {
            return 0;
        }
    }
}
