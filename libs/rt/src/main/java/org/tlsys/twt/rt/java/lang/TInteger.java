package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.Integer")
public class TInteger extends Number {

    public static final int MIN_VALUE = Script.code("Number.MIN_VALUE");

    public static final int MAX_VALUE = Script.code("Number.MAX_VALUE");

    private final int value;

    public TInteger(int value) {
        this.value = value;
    }

    public TInteger(String s) {
        this.value = parseInt(s, 10);
    }

    public static int parseInt(String s) {
        return parseInt(s, 10);
    }

    public static int parseInt(String s, int radix) {
        boolean b = Script.code("isNaN(",s,")");
        if (b)
            throw new NumberFormatException(s);
        return Script.code("parseInt(",s,",",radix,")");
    }

    public static String toString(int value) {
        return toString(value, 10);
    }

    public static String toString(int value, int radix) {
        return Script.code(value,".toString(",radix,")");
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }
}
