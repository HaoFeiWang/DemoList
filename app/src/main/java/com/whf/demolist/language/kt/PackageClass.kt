package com.whf.demolist.language.kt

import android.util.Log

const val TAG = "KT_Test_"

var num = 0

//包级函数测试，在同一个包里面不需要导包，否则也需要导包
fun testPackageMethod() {
    Log.d(TAG, "invoke testPackageMethod!")
}

//给Any类扩展属性
val Any.tag: String
    get() = TAG + this.javaClass