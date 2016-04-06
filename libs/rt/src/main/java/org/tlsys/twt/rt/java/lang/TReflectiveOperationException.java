package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.lang.ReflectiveOperationException")
@ReplaceClass(java.lang.ReflectiveOperationException.class)
public class TReflectiveOperationException extends Exception {
    private static final long serialVersionUID = 4690764048585796909L;

    public TReflectiveOperationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public TReflectiveOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message
     * and cause.
     *
     * <p>Note that the detail message associated with
     * {@code cause} is <em>not</em> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public TReflectiveOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public TReflectiveOperationException(Throwable cause) {
        super(cause);
    }
}
