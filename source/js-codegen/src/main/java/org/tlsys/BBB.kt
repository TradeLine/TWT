package org.tlsys

import java.util.HashMap

class BBB(a: Int, c: Int, d: Int) {
    init {
        var a = a
        testAAA(a, c, d)
        F()

        do {
            if (a > 999999) {
                testAAA(111, 111, 111)
            } else {
                testAAA(222, 222, 222)
            }

            a = a - 22222
        } while (a > 999999)
    }
}
