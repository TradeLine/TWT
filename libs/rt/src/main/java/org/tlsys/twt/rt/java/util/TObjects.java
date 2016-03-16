package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.util.Objects")
public final class TObjects {
    private TObjects() {
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static <T> T requireNonNull(T t) {
        if (t == null)
            throw new NullPointerException();
        return t;
    }

    public static <T> T requireNonNull(T t, String description) {
        if (t == null)
            throw new NullPointerException(description);
        return t;
    }

    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }
}
