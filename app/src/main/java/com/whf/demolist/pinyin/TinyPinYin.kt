package com.whf.demolist.pinyin

import android.text.TextUtils
import android.util.Log
import com.github.promeg.pinyinhelper.Pinyin

class TinyPinYin {

    fun convertToFirstUpperSpell(chinese: String): String {
        if (TextUtils.isEmpty(chinese) || TextUtils.isEmpty(chinese.trim({ it <= ' ' }))) {
            return "z"
        }

        val nameChar = chinese.trim({ it <= ' ' }).toCharArray()
        if (nameChar.size == 0) {
            return "z"
        }

        var firstLetter: Char = 0.toChar()
        // chinese ascii code
        if (Character.toString(nameChar[0]).matches("[\\u4E00-\\u9FA5]+".toRegex())) {
            try {
                val strArray = Pinyin.toPinyin(nameChar[0])
                if (null == strArray || strArray.length == 0) {
                    return "z"
                }
                firstLetter = strArray[0]
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