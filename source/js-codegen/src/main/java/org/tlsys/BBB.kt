package org.tlsys

import java.util.HashMap

class BBB(a: Int, c: Int, d: Int) {
    init {
        var b = a
        testAAA(b, c, d)
        F()
        if (b > 1000)
            testAAA(111, 111, 111)

/*
        if (b>1000) {
            if (b<=500) {
                testAAA(111, 111, 111)
            }
            else {
                testAAA(222, 222, 222)
            }
        } else {
            testAAA(333,333,333)
        }
        */
    }
}
