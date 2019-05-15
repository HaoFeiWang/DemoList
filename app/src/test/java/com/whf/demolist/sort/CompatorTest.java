package com.whf.demolist.sort;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompatorTest implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        //第一个是数字，第二个不是数字，返回1
        //A<B 返回1
        if (isDigitStart(o1) && !isDigitStart(o2)) {
            return 1;
        }

        if (!isDigitStart(o1) && isDigitStart(o2)) {
            return -1;
        }

        String contact0FirstLetter = Cn2SpellUtil.convertToFirstUpperSpell(o1);
        String contact1FirstLetter = Cn2SpellUtil.convertToFirstUpperSpell(o2);

        return contact0FirstLetter.compareTo(contact1FirstLetter);
    }


    public static boolean isDigitStart(String text) {
        Pattern pattern = Pattern.compile("^\\d+");
        Matcher matcher = pattern.matcher(text.charAt(0) + "");
        return matcher.matches();
    }

}
