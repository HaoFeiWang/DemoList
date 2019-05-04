package com.whf.demolist.language.kt

class Student: Person() {

    /**
     * 只用 open 的类才能被继承
     * 只有 open 的方法才能被重写
     * 重写 open 方法，改方法仍会 open
     */
    override fun eat(){

    }
}