package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;


@JSClass
@ReplaceClass(java.lang.IndexOutOfBoundsException.class)
public class TIndexOutOfBoundsException extends RuntimeException {
    public TIndexOutOfBoundsException() {
        super();
    }

    /**
     * Constructs an <code>IndexOutOfBoundsException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public TIndexOutOfBoundsException(String s) {
        super(s);
    }
}
