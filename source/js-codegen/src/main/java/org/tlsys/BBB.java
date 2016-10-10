package org.tlsys;

/**
 * Created by subochev on 26.09.16.
 */
public class BBB {
    public BBB(int a, int c, int d) {
        AAAKt.testAAA(a, c, d);

        do {

            if (a>999999) {
                AAAKt.testAAA(1,2,3);
            } else {
                AAAKt.testAAA(3,2,1);
            }

            a=a-22222;
        } while (a>999999);



        AAAKt.testAAA(1, 2, 3);
    }
}
