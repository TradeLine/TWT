package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.lang.Enum")
@ReplaceClass(java.lang.Enum.class)
public class TEnum {
    private int ordinal;
    private String name;

    public TEnum(String name, int ordinal) {
        this.ordinal = ordinal;
        this.name = name;
    }

    public TEnum() {
    }

    public int ordinal() {
        return ordinal;
    }

    public String name() {
        return name;
    }

    @Override
    public final boolean equals(Object other) {
        return this==other;
    }

    @Override
    public String toString() {
        return name;
    }
}
