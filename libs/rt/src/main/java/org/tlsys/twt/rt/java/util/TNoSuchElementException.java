package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.util.NoSuchElementException")
@ReplaceClass(java.util.NoSuchElementException.class)
public class TNoSuchElementException extends RuntimeException {
    /**
     * Constructs a <code>NoSuchElementException</code> with <tt>null</tt>
     * as its error message string.
     */
    public TNoSuchElementException() {
        super();
    }

    /**
     * Constructs a <code>NoSuchElementException</code>, saving a reference
     * to the error message string <tt>s</tt> for later retrieval by the
     * <tt>getMessage</tt> method.
     *
     * @param   s   the detail message.
     */
    public TNoSuchElementException(String s) {
        super(s);
    }
}
