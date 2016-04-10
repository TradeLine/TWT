package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.boxcastadapter.LongAdapter;

@JSClass
@ReplaceClass(Long.class)
@CastAdapter(LongAdapter.class)
public class TLong extends Number {

    public static final long MIN_VALUE = CastUtil.toLong(Script.code("Number.MIN_VALUE"));
    public static final long MAX_VALUE = CastUtil.toLong(Script.code("Number.MAX_VALUE"));

    private final long value;

    public TLong(int value) {
        this.value = value;
    }

    public TLong(long value) {
        this.value = value;
    }
    public TLong(TLong value) {
        this.value = value.longValue();
    }

    public TLong(String s) {
        this(parseLong(s));
    }

    public static long parseLong(String s) {
        return parseLong(s, 10);
    }

    public static long parseLong(String s, int radix) {
        boolean b = CastUtil.toBoolean(Script.code("isNaN(", s, ")"));
        if (b)
            throw new NumberFormatException(s);
        return CastUtil.toLong(Script.code("parseInt(", s, ",", CastUtil.toObject(radix), ")"));
    }

    public static String toString(long value) {
        return toString(value, 10);
    }

    public static String toString(long value, int radix) {
        return Script.code(CastUtil.toObject(value), ".toString(", CastUtil.toObject(radix), ")");
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

    public static TLong fromjava_lang_Object(Object value) {
        return CastUtil.cast(value);
    }
}
