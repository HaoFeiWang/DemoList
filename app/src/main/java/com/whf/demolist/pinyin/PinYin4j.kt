package com.whf.demolist.pinyin

import android.text.TextUtils
import android.util.Log
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType

class PinYin4j {

    fun convertToFirstUpperSpell(chinese: String): String {
        if (TextUtils.isEmpty(chinese) || TextUtils.isEmpty(chinese.trim({ it <= ' ' }))) {
            return "z"
        }

        val nameChar = chinese.trim({ it <= ' ' }).toCharArray()
        if (nameChar.size == 0) {
            return "z"
        }
        // default format
        val defaultFormat = HanyuPinyinOutputFormat()
        defaultFormat.caseType = HanyuPinyinCaseType.UPPERCASE
        defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE

        var firstLetter: Char = 0.toChar()
        // chinese ascii code
        if (Character.toString(nameChar[0]).matches("[\\u4E00-\\u9FA5]+".toRegex())) {
            try {
                val strArray = PinyinHelper.toHanyuPinyinStringArray(nameChar[0], defaultFormat)
                if (null == strArray || strArray.size == 0) {
                    return "z"
                }
                firstLetter = strArray[0][0]
            } catch (e: Exception) {
                Log.e("Cn2SpellUtil", "convert to first spell error = $e")
                // 异常情况排在最后
                return "z"
            }

        } else {
            firstLetter = Character.toUpperCase(nameChar[0])
        }

        return firstLetter.toString()

    }

}
