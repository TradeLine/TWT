package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.lang.Enum")
@ReplaceClass(java.lang.Enum.class)
public class TEnum {
    public int ordinal;
    public String name;

    public TEnum(String name, int ordinal) {
        this.ordinal = ordinal;
        this.name = name;
    }

    public int ordinal() {
        return ordinal;
    }

    public String name() {
        return name;
    }
}
