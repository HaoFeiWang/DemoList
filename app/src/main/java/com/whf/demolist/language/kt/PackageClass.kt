package com.whf.demolist.language.kt

import android.text.TextUtils
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

    //遍历map，解构
    for ((key, value) in maps) {
        Log.d(TAG, "test for map key = $key , value = $value")
    }
}

/**
 * when操作符
 */
fun testWhen(age: Int): String =
        when {
            age < 18 -> "未成年"
            age >= 18 -> "成年"
            else -> "骚年"
        }

/**
 * in 操作符测试
 */
fun testIn() {

    3 in 1..5

    "a" in arrayListOf("a", "b", "c")

    "b" !in arrayListOf("a", "b", "c")
}

/**
 * try-cache 测试
 */
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

/**
 * 集合方法测试
 */
fun testCollection() {
    val set = hashSetOf(1, 2, 3)
    Log.d(TAG, "hashSetOf class = ${set.javaClass.simpleName} ")

    //Kotlin针对集合的扩展方法
    Log.d(TAG, set.joinToString(prefix = "[", postfix = "]"))
    val maxValue = set.max()
    val last = set.last()

    val array = arrayOf("b", "c", "d")
    //可变参数中如果是数组，需要用展开运算符将数组展开
    val list = listOf("a", *array)

    val arrayList = arrayListOf(1, 2, 3)
    Log.d(TAG, "arrayListOf class = ${arrayList.javaClass.simpleName} ")

    //中缀符号调用 to 函数，原为：1.to("a")返回Pair对象
    val hashMap = hashMapOf(1 to "a", 2 to "b", 3 to "c")
    Log.d(TAG, "hashMapOf class = ${hashMap.javaClass.simpleName} ")

}

/**
 * 字符串扩展函数测试
 */
fun testString() {
    val str = "12.345-6.A"
    Log.d(TAG, "split result：${str.split("""\.|-""".toRegex())}")
    Log.d(TAG, "split result2: ${str.split('.', '-')}")

    val path = "/Users/whf/kotlin/learn.pdf"
    Log.d(TAG, "directory: ${path.substringBeforeLast('/')}")
    Log.d(TAG, "fullName: ${path.substringAfterLast('/')}")
}

/**
 * 局部方法测试
 */
fun testPartMethod(person: Person) {
    //可读性更强的方式推荐将其提取为 Person 的扩展方法
    fun checkParam(value: String): Boolean {
        if (TextUtils.isEmpty(value)) {
            Log.d(TAG, "$person param check fail!")
            return false
        }
        return true
    }

    if (checkParam(person.idCard)
            && checkParam(person.name)) {
        Log.d(TAG, "$person param check success!")
    }
}

/**
 * 继承测试
 */
fun testExtend(){
    val student = Student()
    student.idCard = "123"
    student.name = "Jack"
    student.age = 12
    student.sex = 1
}

/**
 * 测试 internal 操作符
 */
fun testInternal(){
    val dog = Dog()
    val name = dog.name
}