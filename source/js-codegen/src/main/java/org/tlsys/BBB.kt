package org.tlsys

import java.util.HashMap

class BBB(a: Int, c: Int, d: Int) {
    init {
        var a = a
        testAAA(a, c, d)
        F()

        if (a > 999999) {
            if (a > 8888) {
                while (a > 0) {
                    testAAA(111, 111, 111)
                    a=a-1
                }
            }else
                testAAA(222, 222, 222)
        } else {
            testAAA(333, 333, 333)
        }
    }
}
