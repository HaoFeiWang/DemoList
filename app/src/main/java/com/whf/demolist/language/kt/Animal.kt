package com.whf.demolist.language.kt

import android.util.Log

interface Animal {
    fun eat()

    /**
     * 和 Java 8 一样，接口支持默认方法，Java 8 还需要标注default关键字
     * 因为Kotlin是基于Java 6，所以这里会编译成静态内部类的静态方法
     */
    fun breathe(){
        Log.d(TAG,"animal breathe!")
    }
}