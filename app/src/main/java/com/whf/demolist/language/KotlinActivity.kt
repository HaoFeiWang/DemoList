package com.whf.demolist.language

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.whf.demolist.R
import com.whf.demolist.language.kt.testCollection

import com.whf.demolist.language.kt.tag as TAG

//包级函数不在同一个包也需要导包
import com.whf.demolist.language.kt.testFor
import com.whf.demolist.language.kt.testTry

//不需要 findViewById 的导包
import kotlinx.android.synthetic.main.activity_kotlin.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        package_method_for.setOnClickListener {
            testFor()
        }

        package_method_try.setOnClickListener {
            testTry()
        }

        package_method_collection.setOnClickListener {
            testCollection()
        }

        package_method_collection.setOnClickListener {
            testCollection()
        }

        package_method_coroutine.setOnClickListener {
            testCoroutine()
        }
    }

    private fun testCoroutine() {
        // 在后台启动一个新的协程并继续
        val coroutineStatus = GlobalScope.launch {
            //执行在非主线程
            Log.d(TAG, "coroutine start!")
            //非阻塞的等待 1 秒钟（默认时间单位是毫秒）
            delay(5000)
            Log.d(TAG, "coroutine finish!")

        }
        Log.d(TAG, "coroutine main thread!")


        runBlocking {
            //执行在主线程
            Log.d(TAG, "coroutine2 start!")
            //非阻塞的等待 10 秒钟（默认时间单位是毫秒），并不会ANR
            delay(10000)
            Log.d(TAG, "coroutine2 finish!")
        }
        Log.d(TAG, "coroutine2 main thread!")
    }
}

