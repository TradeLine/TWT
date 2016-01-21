package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.Math")
public final class TMath {
    private TMath() {
    }

    public static int round(float a) {
        return Script.code("Math.round(",a,")");
    }

    public static long round(double a) {
        return Script.code("Math.round(",a,")");
    }

    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }

    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }
}
