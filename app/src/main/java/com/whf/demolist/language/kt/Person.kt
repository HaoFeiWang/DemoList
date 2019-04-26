package com.whf.demolist.language.kt

/**
 * 数据类
 *
 * 默认实现了copy、toString、hashCode.....等方法
 * 默认生成componentN方法，用于解构对象
 * allOpen和noArg插件（因为数据类默认生成的字节码是final类且没有无参构造器，这两个插件是在编译期对字节码进行处理的）
 */
data class Person(
        var age: Int,
        var name: String,
        var sex: Boolean
)