package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(Double.class)
public class TDouble extends Number {
    public static final double POSITIVE_INFINITY = CastUtil.toDouble(Script.code("Number.POSITIVE_INFINITY"));
    public static final double NEGATIVE_INFINITY = CastUtil.toDouble(Script.code("Number.NEGATIVE_INFINITY"));
    public static final double NaN = CastUtil.toDouble(Script.code("Number.NaN"));

    public static final double MAX_VALUE = CastUtil.toDouble(Script.code("Number.MAX_VALUE"));
    public static final double MIN_VALUE = CastUtil.toDouble(Script.code("Number.MIN_VALUE"));
    public static final int MAX_EXPONENT = 1023;
    public static final int MIN_EXPONENT = -1022;

    public static Double valueOf(String s) throws NumberFormatException {
        return new Double(parseDouble(s));
    }
    public static Double valueOf(double d) {
        return new Double(d);
    }

    public static double parseDouble(String s) throws NumberFormatException {
        double d = Script.code("parseFloat(",s,")");
        if (isNaN(d))
                throw new NumberFormatException(s);
        return d;
    }

    public static boolean isNaN(double v) {
        return CastUtil.toBoolean(Script.code("isNaN(",CastUtil.toObject(v),")"));
    }

    public static boolean isInfinite(double v) {
        return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    public static boolean isFinite(double d) {
        return CastUtil.toBoolean(Script.code("isFinite(",CastUtil.toObject(d),")"));
    }

    private final double value;

    public TDouble(double value) {
        this.value = value;
    }

    public TDouble(String s) throws NumberFormatException {
        value = parseDouble(s);
    }

    public boolean isNaN() {
        return isNaN(value);
    }

    public boolean isInfinite() {
        return isInfinite(value);
    }

    public String toString() {
        return toString(value);
    }

    public byte byteValue() {
        return (byte)value;
    }

    public short shortValue() {
        return (short)value;
    }

    public int intValue() {
        return (int)value;
    }

    public long longValue() {
        return (long)value;
    }

    public float floatValue() {
        return (float)value;
    }

    public double doubleValue() {
        return value;
    }

    public static String toString(double d) {
        return Script.code(CastUtil.toObject(d),".toString()");
    }
}
