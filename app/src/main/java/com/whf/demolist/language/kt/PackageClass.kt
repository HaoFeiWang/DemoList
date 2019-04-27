package com.whf.demolist.language.kt

import android.util.Log
import java.lang.Exception

/**
 * 顶层函数、属性，在包外访问的时候需要导包
 */
const val TAG = "KT_Test_"

//给Any类扩展属性，实际只是提供了一个getTag的方法
val Any.tag: String
    get() = TAG + this.javaClass.simpleName

fun testFor() {
    //区间是左闭右闭
    for (i in 1..3) {
        Log.d(TAG, "test for i: $i")
    }

    //区间为左闭右开
    for (j in 1 until 3) {
        Log.d(TAG, "test for j: $j")
    }

    //步长为2，输出为1 3 5 7 9
    for (k in 1..6 step 2) {
        Log.d(TAG, "test for k: $k")
    }

    //遍历list
    val list = arrayListOf("a", "b", "c")
    for ((index, value) in list.withIndex()) {
        Log.d(TAG, "$index = $value")
    }

    //创建map
    val maps = HashMap<Int, String>()
    maps[0] = "a"
    maps[1] = "b"
    maps[2] = "c"

    //遍历map
    for ((key, value) in maps) {
        Log.d(TAG, "test for map key = $key , value = $value")
    }
}


fun testWhen(age: Int): String =
        when {
            age < 18 -> "未成年"
            age >= 18 -> "成年"
            else -> "骚年"
        }

fun testIn() {

    3 in 1..5

    "a" in arrayListOf("a", "b", "c")

    "b" !in arrayListOf("a", "b", "c")
}


fun testTry() {
    Log.d(TAG, "start test try!")
    val number: Int = try {
        Integer.parseInt("abc")

    } catch (e: Exception) {
        //return的是整个testTry方法
        //return
        1
    }
    Log.d(TAG, "number is $number")
}


fun testCollection() {


    val set = hashSetOf(1, 2, 3)
    Log.d(TAG, "hashSetOf class = ${set.javaClass.simpleName} ")

    //Kotlin针对集合的扩展方法
    Log.d(TAG, set.joinToString(prefix = "[", postfix = "]"))
    val maxValue = set.max()
    val last = set.last()

    val array = arrayOf("b","c","d")
    //可变参数中如果是数组，需要用展开运算符将数组展开
    val list = listOf("a",*array)

    val arrayList = arrayListOf(1, 2, 3)
    Log.d(TAG, "arrayListOf class = ${arrayList.javaClass.simpleName} ")

    //中缀符号调用 to 函数，原为：1.to("a")返回Pair对象
    val hashMap = hashMapOf(1 to "a", 2 to "b", 3 to "c")
    Log.d(TAG, "hashMapOf class = ${hashMap.javaClass.simpleName} ")

}