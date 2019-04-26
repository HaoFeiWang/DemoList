package com.whf.demolist.language

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.whf.demolist.R

import com.whf.demolist.language.kt.tag as TAG


//包级函数不在同一个包也需要导包
import com.whf.demolist.language.kt.testFor

//不需要 findViewById 的导包
import kotlinx.android.synthetic.main.activity_kotlin.*


class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        package_method.setOnClickListener {
            testFor()
        }
    }
}
