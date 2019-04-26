package com.whf.demolist.language.kt

/**
 * 单例类
 */
object Teacher {

    var age = 18

    /**
     * 只是多了个getLevel方法，成员变量并没有增加
     */
    val level: String
        get() = if (age > 18) "未成年人" else "成年人"
}