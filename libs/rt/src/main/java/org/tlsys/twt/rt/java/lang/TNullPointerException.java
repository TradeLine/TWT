package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.lang.NullPointerException")
@ReplaceClass(java.lang.NullPointerException.class)
public class TNullPointerException extends RuntimeException {
    public TNullPointerException() {
        super();
    }

    /**
     * Constructs a {@code NullPointerException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public TNullPointerException(String s) {
        super(s);
    }
}
