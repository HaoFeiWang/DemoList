package com.whf.demolist.language.kt

/**
 * 密封类，默认为 open 的
 */
sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Num, val right: Num) : Expr()
}