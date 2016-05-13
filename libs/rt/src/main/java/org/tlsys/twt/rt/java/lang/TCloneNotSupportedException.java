package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(CloneNotSupportedException.class)
public class TCloneNotSupportedException extends Exception {
    public TCloneNotSupportedException() {
        super();
    }

    /**
     * Constructs a <code>CloneNotSupportedException</code> with the
     * specified detail message.
     *
     * @param s the detail message.
     */
    public TCloneNotSupportedException(String s) {
        super(s);
    }
}
