package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(Float.class)
public class TFloat {
    private final float value;
    public TFloat(float value) {
        this.value = value;
    }
}
