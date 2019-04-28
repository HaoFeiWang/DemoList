package com.whf.demolist.language

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.whf.demolist.R
import com.whf.demolist.language.kt.testCollection

//包级函数不在同一个包也需要导包
import com.whf.demolist.language.kt.testFor
import com.whf.demolist.language.kt.testTry
import com.whf.demolist.language.kt.TAG

//不需要 findViewById 的导包
import kotlinx.android.synthetic.main.activity_kotlin.*
import kotlinx.coroutines.*


class KotlinActivity : AppCompatActivity() {

    private val tag = TAG + this.javaClass.simpleName

    private var coroutine1: Job? = null

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

        start_coroutine.setOnClickListener {
            testCoroutine()
        }

        cancel_coroutine.setOnClickListener {
            Log.d(tag, "cancel coroutine1!")
            coroutine1?.cancel()
        }

        flag_coroutine.setOnClickListener {
            flagCoroutine()
        }
    }

    private fun flagCoroutine() {
        runBlocking {

            //协程一直在跑计算，没有去检查取消标记，那它就无法取消，会将20全部输出
            /*val job = GlobalScope.launch {
                var i = 0
                while (i<20) {
                    Log.d(TAG,"job is ${i++}")
                }
            }*/

            //检查取消标志位，只输出了3个数
            val job = GlobalScope.launch {
                var i = 0
                while (isActive && i<20) {
                    Log.d(TAG, "job is ${i++}")
                }
            }

            Log.d(TAG, "start cancel job!")
            // 取消子协程，等待它结束
            job.cancelAndJoin()
            Log.d(TAG, "end cancel job!")
        }
    }

    private fun testCoroutine() {
        // 在后台启动一个新的协程并继续
        coroutine1 = GlobalScope.launch {
            //执行在子线程
            Log.d(tag, "coroutine1 start!")
            //非阻塞的等待 1 秒钟（默认时间单位是毫秒）
            delay(6000)
            Log.d(tag, "coroutine1 end!")
        }


        //锁住线程，直到内部协程代码执行完成
        Log.d(tag, "=== runBlocking start === ")
        runBlocking {
            //执行在主线程
            Log.d(tag, "coroutine2 start!")
            //非阻塞的等待 10 秒钟（默认时间单位是毫秒），并不会ANR
            delay(2000)
            Log.d(tag, "coroutine2 end!")

            val coroutine3 = GlobalScope.launch {
                createJob()
            }

            Log.d(tag, "coroutine4 start")
            //一直等到 coroutine3 执行完成
            coroutine3.join()
            Log.d(tag, "coroutine4 end")
        }
        Log.d(tag, "=== runBlocking end ===")
    }

    //协程方法需要用suspend修饰
    private suspend fun createJob() {
        Log.d(tag, "coroutine3 start")
        delay(2000)
        Log.d(tag, "coroutine3 end")
    }
}

