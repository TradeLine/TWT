package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ForceInject;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.boxcastadapter.IntAdapter;

@JSClass
@ReplaceClass(Integer.class)
@CastAdapter(IntAdapter.class)
public final class TInteger extends Number {

    public static final int MIN_VALUE = 0x80000000;

    public static final int MAX_VALUE = 0x7fffffff;
    private static final long serialVersionUID = 1887258139513155261L;

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
        boolean b = CastUtil.toBoolean(Script.code("isNaN(", s, ")"));
        if (b)
            throw new NumberFormatException(s);
        return CastUtil.toInt(Script.code("parseInt(", s, ",", radix, ")"));
    }

    public static String toString(int value) {
        return toString(value, 10);
    }

    public static String toString(int value, int radix) {
        return Script.code(CastUtil.toObject(value), ".toString(", CastUtil.toObject(radix), ")");
    }

    public static TInteger fromjava_lang_Object(Object value) {
        return CastUtil.cast(value);
    }

    @ForceInject
    @Override
    public int intValue() {
        return value;
    }

    @ForceInject
    @Override
    public long longValue() {
        return value;
    }

    @ForceInject
    @Override
    public float floatValue() {
        return value;
    }

    @ForceInject
    @Override
    public double doubleValue() {
        return value;
    }
}
