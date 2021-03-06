package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.boxcastadapter.FloatAdapter;

@JSClass
@ReplaceClass(Float.class)
@CastAdapter(FloatAdapter.class)
public class TFloat extends Number {
    private static final long serialVersionUID = 6217293767823110454L;
    private final float value;
    public TFloat(float value) {
        this.value = value;
    }

    public static String toString(float value) {
        return Script.code(CastUtil.toObject(value), ".toString()");
    }

    public static TFloat fromjava_lang_Object(Object value) {
        return CastUtil.cast(value);
    }

    @Override
    public int intValue() {
        return CastUtil.toInt(Script.code("Math.floor(", CastUtil.toObject(value), ")"));
    }

    @Override
    public long longValue() {
        return CastUtil.toLong(Script.code("Math.floor(", CastUtil.toObject(value), ")"));
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return CastUtil.toDouble(CastUtil.toObject(value));
    }

    public String toString() {
        return toString(value);
    }

    public static float parseFloat(String s) {
        boolean b = CastUtil.toBoolean(Script.code("isNaN(", s, ")"));
        if (b)
            throw new NumberFormatException(s);
        return CastUtil.toFloat(Script.code("parseFloat(", s, ")"));
    }
}
