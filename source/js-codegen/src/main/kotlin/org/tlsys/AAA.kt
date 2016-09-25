package org.tlsys

class AAA(a: Int, c: Int, d: Int) {
    init {
        testAAA(a,c,if (d>20) 30 else 40)
    }
}

fun testAAA(a:Int, b:Int, c:Int):Int = a+b+c
fun doit(a:String){
    println(a)
}