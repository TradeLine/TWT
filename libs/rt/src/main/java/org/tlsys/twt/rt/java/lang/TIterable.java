package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

import java.util.Iterator;

@JSClass
@ClassName("java.lang.Iterable")
public interface TIterable<T> {
    public Iterator<T> iterator();
}
