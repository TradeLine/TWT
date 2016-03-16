package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.Long")
public class TLong extends Number {

    public static final int MIN_VALUE = Script.code("Number.MIN_VALUE");
    public static final int MAX_VALUE = Script.code("Number.MAX_VALUE");

    private final long value;

    public TLong(long value) {
        this.value = value;
    }
    public TLong(TLong value) {
        this.value = value.longValue();
    }

    public TLong(String s) {
        this(parseLong(s));
    }

    @Override
    public int intValue() {
        return (int) value;
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

    public static long parseLong(String s) {
        return parseLong(s, 10);
    }

    public static long parseLong(String s, int radix) {
        boolean b = Script.code("isNaN(",s,")");
        if (b)
            throw new NumberFormatException(s);
        return Script.code("parseInt(", s, ",", radix, ")");
    }
}
