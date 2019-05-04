package com.whf.demolist.language.kt

class Dog : Animal {

    override fun eat() {

    }

    override fun breathe() {
        //如果实现了两个接口都有该方法，则必须重写该方法,否则无法编译
        //使用 super 尖括号加上父类的名字，表明想要调用哪一个父类的方法
        super<Animal>.breathe()
    }
}