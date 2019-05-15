package com.whf.demolist.pinyin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.whf.demolist.R
import kotlinx.android.synthetic.main.activity_pin_yin.*
import java.util.ArrayList
import com.whf.demolist.language.kt.TAG

class PinYinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_yin)

        pin_yin_4j.setOnClickListener {
            convertPinyin4j()
        }

        tiny_pin_yin.setOnClickListener {
            convertTinyPinyin()
        }
    }

    //Pinyin4j占用内存高达10M
    fun convertPinyin4j() {
        val srcString = getSrc()
        val resultString = ArrayList<String>()
        val startTime = System.currentTimeMillis()

        for (str in srcString) {
            val pinYin4j = PinYin4j()
            val result = pinYin4j.convertToFirstUpperSpell(str)
            resultString.add(result)
        }

        Log.d(TAG, resultString.toString())
        Log.d(TAG, "time = "+(System.currentTimeMillis() - startTime))
    }

    //TinyPinYin几乎不占用内存
    fun convertTinyPinyin() {
        val srcString = getSrc()
        val resultString = ArrayList<String>()
        val startTime = System.currentTimeMillis()

        for (str in srcString) {
            val tinyPinYin = TinyPinYin()
            val result = tinyPinYin.convertToFirstUpperSpell(str)
            resultString.add(result)
        }
        Log.d(TAG, resultString.toString())
        Log.d(TAG, "time = "+(System.currentTimeMillis() - startTime))
    }

    private fun getSrc(): ArrayList<String> {
        //[C, A, A, C, W, C, B, B, B, 1, 8, 9, L, M, A, A, B, C, C, C, D, D, D, L, L, W]
        val srcString = ArrayList<String>()
        srcString.add("cd")
        srcString.add("ab")
        srcString.add("Aa")
        srcString.add("Cc")
        srcString.add("文明")
        srcString.add("岑岑")
        srcString.add("bB")
        srcString.add("Bayi1")
        srcString.add("伯伯")
        srcString.add("123")
        srcString.add("888")
        srcString.add("9654")
        srcString.add("赖赖")
        srcString.add("妈妈")
        srcString.add("aA")
        srcString.add("阿姨")
        srcString.add("Bb")
        srcString.add("Cc")
        srcString.add("cd")
        srcString.add("慈慈")
        srcString.add("Dd")
        srcString.add("dm")
        srcString.add("戴戴")
        srcString.add("LI")
        srcString.add("伶俐")
        srcString.add("Ww")
        return srcString
    }

}
