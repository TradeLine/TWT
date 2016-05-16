package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(InternalError.class)
public class TInternalError extends TError {
    public TInternalError() {
        super();
    }

    /**
     * Constructs an <code>InternalError</code> with the specified
     * detail message.
     *
     * @param   message   the detail message.
     */
    public TInternalError(String message) {
        super(message);
    }


    /**
     * Constructs an {@code InternalError} with the specified detail
     * message and cause.  <p>Note that the detail message associated
     * with {@code cause} is <i>not</i> automatically incorporated in
     * this error's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.8
     */
    public TInternalError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code InternalError} with the specified cause
     * and a detail message of {@code (cause==null ? null :
     * cause.toString())} (which typically contains the class and
     * detail message of {@code cause}).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.8
     */
    public TInternalError(Throwable cause) {
        super(cause);
    }
}
