package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.boxcastadapter.ShortAdapter;

@JSClass
@ReplaceClass(Short.class)
@CastAdapter(ShortAdapter.class)
public class TShort extends Number {

    public static final short   MIN_VALUE = -32768;
    public static final short   MAX_VALUE = 32767;
    private static final long serialVersionUID = -532933355093144222L;

    private final short val;

    public TShort(short val) {
        this.val = val;
    }

    public static String toString(short s) {
        return Integer.toString((int) s, 10);
    }

    @Override
    public int intValue() {
        return (int)val;
    }

    @Override
    public long longValue() {
        return (long)val;
    }

    @Override
    public float floatValue() {
        return (float)val;
    }

    @Override
    public double doubleValue() {
        return (double)val;
    }

    public TShort fromjava_lang_Object(Object value) {
        return CastUtil.cast(value);
    }

    public String toString() {
        return toString(val);
    }
}
