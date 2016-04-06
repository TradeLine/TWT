package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(java.lang.Character.class)
public class TCharacter {
    public static final int MIN_RADIX = 2;
    public static final int MAX_RADIX = 36;
    public static final char MIN_VALUE = '\u0000';
    public static final char MAX_VALUE = '\uFFFF';

    private final char value;

    public TCharacter(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return CastUtil.cast(CastUtil.toObject(value));
    }
}
