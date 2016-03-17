package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(Short.class)
public class TShort extends Number {

    public static final short   MIN_VALUE = -32768;
    public static final short   MAX_VALUE = 32767;

    private final short val;

    public TShort(short val) {
        this.val = val;
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
}
