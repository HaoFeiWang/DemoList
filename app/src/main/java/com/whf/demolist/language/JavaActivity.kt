package com.whf.demolist.language

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.whf.demolist.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_java.*
import java.io.*
import java.lang.StringBuilder
import java.nio.channels.Channel
import java.nio.channels.FileLock


class JavaActivity : AppCompatActivity() {

    private val tag = "Test_"
    @Volatile
    private var writeNum = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_java)

        tv_file_lock_write.setOnClickListener { testFileLockWrite() }
        tv_file_lock_read.setOnClickListener { testFileLockRead() }

    }

    private fun testFileLockRead() {
        Log.d(tag, "test file lock read = $writeNum")
        Observable
                .create<String> {
                    var accessFile: RandomAccessFile? = null
                    var channel: Channel? = null
                    var fileLock: FileLock? = null
                    var fileReader: FileReader? = null

                    try {
                        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/demoList/"
                        val folder = File(folderPath)
                        if (!folder.exists()) {
                            folder.mkdirs()
                        }

                        val lockFilePath = "$folderPath/test_local.xml"
                        val lockFile = File(lockFilePath)
                        if (!lockFile.exists()) {
                            lockFile.createNewFile()
                        }

                        //获取文件锁
                        //快速执行6次该方法，验证并发写的问题，由结果可知一个文件不可能同时被多个线程写
                        //有并发：222222222233333333334444444444555555555566666666667777777777
                        //无并发：2222222222
//                        accessFile = RandomAccessFile(lockFilePath, "rw")
//                        channel = accessFile.channel
//                        fileLock = channel.tryLock()
//                        if (fileLock === null) {
//                            it.onError(Throwable(""))
//                            return@create
//                        }


                        val result = StringBuilder()
                        val buffer = CharArray(100)
                        val objectFilePath = "$folderPath/object.txt"

                        var length = 0
                        fileReader = FileReader(objectFilePath)
                        for (i in 1..5) {
                            Thread.sleep(1000)
                            val readSize = fileReader.read(buffer, 0, buffer.size)
                            if(readSize>0){
                                length += readSize
                                result.append(buffer, 0, readSize)
                            }
                        }

                        it.onNext(result.toString())
                        it.onComplete()
                    } finally {
                        Log.d(tag, "release resource!")
                        fileLock?.release()
                        channel?.close()
                        accessFile?.close()
                        fileReader?.close()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: String) {
                        Log.d(tag, "read result: $t !")
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        Log.e(tag, "read file error $e")
                    }

                    override fun onComplete() {
                        Log.d(tag, "file read finish!")
                    }
                })
    }

    private fun testFileLockWrite() {
        writeNum++
        Log.d(tag, "test file lock write = $writeNum")
        Observable
                .create<Int> {
                    val writerStr = "$writeNum"
                    var accessFile: RandomAccessFile? = null
                    var channel: Channel? = null
                    var fileLock: FileLock? = null
                    var fileWriter: FileWriter? = null

                    try {
                        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/demoList/"
                        val folder = File(folderPath)
                        if (!folder.exists()) {
                            folder.mkdirs()
                        }

                        val lockFilePath = "$folderPath/test_local.xml"
                        val lockFile = File(lockFilePath)
                        if (!lockFile.exists()) {
                            lockFile.createNewFile()
                        }

                        //获取文件锁
                        //快速执行6次该方法，验证并发写的问题，由结果可知一个文件不可能同时被多个线程写
                        //有并发：222222222233333333334444444444555555555566666666667777777777
                        //无并发：2222222222
//                        accessFile = RandomAccessFile(lockFilePath, "rw")
//                        channel = accessFile.channel
//                        fileLock = channel.tryLock()
//                        if (fileLock === null) {
//                            it.onError(Throwable(""))
//                            return@create
//                        }

                        val objectFilePath = "$folderPath/object.txt"
                        val objectFile = File(objectFilePath)
                        if (!objectFile.exists()) {
                            objectFile.createNewFile()
                        }

                        fileWriter = FileWriter(objectFile, true)
                        for (i in 1..100) {
                            Thread.sleep(100)
                            fileWriter.write(writerStr)
                            it.onNext(i)
                        }

                        fileWriter.write("\n")
                        it.onComplete()
                    } finally {
                        Log.d(tag, "release resource!")
                        fileLock?.release()
                        channel?.close()
                        accessFile?.close()
                        fileWriter?.close()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Int) {
                        Log.d(tag, "write $t string!")
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        Log.e(tag, "write file error $e")
                    }

                    override fun onComplete() {
                        Log.d(tag, "file write finish!")
                    }
                })
    }
}
