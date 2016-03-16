package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.util.Iterator")
public interface TIterator<T> {
    public boolean hasNext();
    public T next();
    public void remove();
}
