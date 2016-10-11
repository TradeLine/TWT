package org.tlsys;

/**
 * Created by subochev on 26.09.16.
 */
public class BBB {
    public BBB(int a, int c, int d) {
        AAAKt.testAAA(a, c, d);

        do {

            if (a > 999999) {
                AAAKt.testAAA(111, 111, 111);
            } else {
                AAAKt.testAAA(222, 222, 222);
            }

            a = a - 22222;
        } while (a > 999999);


        try {
            AAAKt.testAAA(333, 333, 333);
        } catch (Throwable e) {
            AAAKt.testAAA(444, 444, 444);
        }/* finally {
            AAAKt.testAAA(555, 555, 555);
        }*/
    }
}
