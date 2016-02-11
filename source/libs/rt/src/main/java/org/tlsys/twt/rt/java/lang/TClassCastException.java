package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.ClassCastException")
public class TClassCastException extends RuntimeException {
    public TClassCastException() {
    }

    public TClassCastException(String message) {
        super(message);
    }
}
