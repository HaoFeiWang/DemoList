package com.whf.demolist.language.kt

class Student: Person() {

    /**
     * 只用 open 的类才能被继承
     * 只有 open 的方法才能被重写
     * 重写 open 方法，改方法仍会 open
     */
    override fun eat(){

    }

    /**
     * 内部类默认为 static
     */
    class School{

    }

    /**
     * 非静态内部类需要用 inner 修饰
     */
    inner class Teacher{

        fun getOutter():Student{
            //引用外部类对象
            return this@Student
        }
    }
}