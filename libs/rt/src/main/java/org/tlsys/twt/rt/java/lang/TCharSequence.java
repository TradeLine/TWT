package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(CharSequence.class)
public interface TCharSequence {
    int length();
    char charAt(int index);
    public String toString();
}
