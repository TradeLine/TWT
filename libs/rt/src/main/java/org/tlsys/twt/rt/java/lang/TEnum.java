package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.Enum")
public class TEnum {
    private final int ordinal;
    private final String name;
    public TEnum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }
}
