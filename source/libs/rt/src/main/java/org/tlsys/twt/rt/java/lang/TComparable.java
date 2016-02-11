package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(java.lang.Comparable.class)
public interface TComparable<T> {
    public int compareTo(T o);
}
