package org.tlsys;

/**
 * Created by Субочев Антон on 30.06.2016.
 */
public class AAA {
    public AAA(int a, int c, int d) {
        //int b = a > 10-c ? (a>13?200:300) : 100;

        int b = 1000;
        if (a > 100 || b > 200 && c > 300)
            b = 300;
        else
            b = 500;
        b = a;
        throw new RuntimeException();

        //int b = (a > 10-c ? (a>13?200:300) : 100) - (a > 20-c ? (a>17?500:600) : 700);
        //int b = (a>10 && c>50) ? 99:100;

    }
}
