package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.boxcastadapter.ByteAdapter;

@JSClass
@ReplaceClass(Byte.class)
@CastAdapter(ByteAdapter.class)
public final class TByte extends Number {
    public static byte MAX_VALUE = 127;
    public static byte MIN_VALUE = -128;

    private final byte val;

    public TByte(byte val) {
        this.val = val;
    }

    public TByte(String val) {
        throw new RuntimeException("Constructor Byte(String) not supported yet");
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

    public TByte fromjava_lang_Object(Object value) {
        return CastUtil.cast(value);
    }
}
