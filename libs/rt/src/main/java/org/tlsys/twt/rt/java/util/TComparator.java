package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.util.Comparator")
public interface TComparator<T> {
    int compare(T o1, T o2);
}
