package org.tlsys.twt.rt.java.io;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

import java.io.IOException;

@ClassName("java.io.ObjectStreamException")
@JSClass
public abstract class TObjectStreamException extends IOException {

    private static final long serialVersionUID = 7260898174833392607L;

    /**
     * Create an ObjectStreamException with the specified argument.
     *
     * @param classname the detailed message for the exception
     */
    protected TObjectStreamException(String classname) {
        super(classname);
    }

    /**
     * Create an ObjectStreamException.
     */
    protected TObjectStreamException() {
        super();
    }
}
