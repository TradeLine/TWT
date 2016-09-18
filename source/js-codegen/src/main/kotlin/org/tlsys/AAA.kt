package org.tlsys

class AAA(a: Int, c: Int, d: Int) {
    init {
        //int b = a > 10-c ? (a>13?200:300) : 100;

        var b = 1000
        if (a > 100 || b > 200 && c > 300)
            b = 300
        else
            b = 500
        b = if (b>20) 30 else 40
        testAAA(a,c,d)
        throw RuntimeException("HELLO!!!")

        //int b = (a > 10-c ? (a>13?200:300) : 100) - (a > 20-c ? (a>17?500:600) : 700);
        //int b = (a>10 && c>50) ? 99:100;
    }
/*
    fun aa(a: Int, c: Int, d: Int) {
        var b = 1000
        if (a > 100 || b > 200 && c > 300)
            b = 300
        else
            b = 500
        b = if (a>20) 30 else throw RuntimeException()

        throw RuntimeException()
    }*/
}

fun testAAA(a:Int, b:Int, c:Int):Int = a+b+c