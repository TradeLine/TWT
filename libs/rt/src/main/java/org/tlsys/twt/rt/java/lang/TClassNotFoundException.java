package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.lang.ClassNotFoundException")
@ReplaceClass(java.lang.ClassNotFoundException.class)
public class TClassNotFoundException extends ReflectiveOperationException {
    /**
     * This field holds the exception ex if the
     * ClassNotFoundException(String s, Throwable ex) constructor was
     * used to instantiate the object
     * @serial
     * @since 1.2
     */
    private Throwable ex;

    /**
     * Constructs a <code>ClassNotFoundException</code> with no detail message.
     */
    public TClassNotFoundException() {
        super((Throwable)null);  // Disallow initCause
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public TClassNotFoundException(String s) {
        super(s, null);  //  Disallow initCause
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the
     * specified detail message and optional exception that was
     * raised while loading the class.
     *
     * @param s the detail message
     * @param ex the exception that was raised while loading the class
     * @since 1.2
     */
    public TClassNotFoundException(String s, Throwable ex) {
        super(s, null);  //  Disallow initCause
        this.ex = ex;
    }

    /**
     * Returns the exception that was raised if an error occurred while
     * attempting to load the class. Otherwise, returns <tt>null</tt>.
     *
     * <p>This method predates the general-purpose exception chaining facility.
     * The {@link Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @return the <code>Exception</code> that was raised while loading a class
     * @since 1.2
     */
    public Throwable getException() {
        return ex;
    }

    /**
     * Returns the cause of this exception (the exception that was raised
     * if an error occurred while attempting to load the class; otherwise
     * <tt>null</tt>).
     *
     * @return  the cause of this exception.
     * @since   1.4
     */
    public Throwable getCause() {
        return ex;
    }
}