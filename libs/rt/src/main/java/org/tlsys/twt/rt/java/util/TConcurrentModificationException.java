package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.util.ConcurrentModificationException")
@ReplaceClass(java.util.ConcurrentModificationException.class)
public class TConcurrentModificationException extends RuntimeException {

    /**
     * Constructs a ConcurrentModificationException with no
     * detail message.
     */
    public TConcurrentModificationException() {
    }

    /**
     * Constructs a {@code ConcurrentModificationException} with the
     * specified detail message.
     *
     * @param message the detail message pertaining to this exception.
     */
    public TConcurrentModificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.7
     */
    public TConcurrentModificationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link Throwable#getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A {@code null} value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since 1.7
     */
    public TConcurrentModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
