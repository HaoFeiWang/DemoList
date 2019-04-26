package com.whf.demolist.language.kt

import android.util.Log

const val TAG = "KT_Test_"

//给Any类扩展属性
val Any.tag: String
    get() = TAG + this.javaClass.simpleName


//包级函数测试，在同一个包里面不需要导包，否则也需要导包
fun testFor() {
    //区间是左闭右闭
    for (i in 1..3) {
        Log.d(TAG, "test for i: $i")
    }

    //区间为左闭右开
    for (j in 1 until 3){
        Log.d(TAG, "test for j: $j")
    }

    //步长为2，输出为1 3 5 7 9
    for (k in 1..6 step 2){
        Log.d(TAG, "test for k: $k")
    }

    //遍历list
    val list = arrayListOf("a","b","c")
    for ((index,value) in list.withIndex()){
        Log.d(TAG, "$index = $value")
    }

    //创建map
    val maps = HashMap<Int,String>()
    maps[0] = "a"
    maps[1] = "b"
    maps[2] = "c"

    //遍历map
    for ((key,value) in maps){
        Log.d(TAG, "test for map key = $key , value = $value")
    }
}


fun testWhen(age: Int): String =
        when {
            age < 18 -> "未成年"
            age >= 18 -> "成年"
            else -> "骚年"
        }

fun testIn(){

    3 in 1..5

    "a" in arrayListOf("a","b","c")

    "b" !in arrayListOf("a","b","c")
}

