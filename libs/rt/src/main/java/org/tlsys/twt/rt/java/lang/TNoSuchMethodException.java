package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(NoSuchMethodException.class)
public class TNoSuchMethodException extends ReflectiveOperationException {
    /**
     * Constructs a <code>NoSuchMethodException</code> without a detail message.
     */
    public TNoSuchMethodException() {
        super();
    }

    /**
     * Constructs a <code>NoSuchMethodException</code> with a detail message.
     *
     * @param      s   the detail message.
     */
    public TNoSuchMethodException(String s) {
        super(s);
    }
}
