package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JSClass
@ClassName("java.util.Arrays")
public final class TArrays {
    public static <T> List<T> asList(T... a) {
        ArrayList out = new ArrayList(a.length);
        for (Object b : a) {
            out.add(b);
        }
        return out;
    }

    public static <T> void sort(T[] a, Comparator<? super T> c) {
        for (int c1 = 0; c1 < a.length; c1++)
            for (int c2 = 0; c2 < a.length; c2++) {
                if (c1 == c2)
                    continue;
                int t = c.compare(a[c1], a[c2]);
                if (t > 0) {
                    T o = a[c1];
                    a[c1] = a[c2];
                    a[c2] = o;
                }
            }
    }
}
