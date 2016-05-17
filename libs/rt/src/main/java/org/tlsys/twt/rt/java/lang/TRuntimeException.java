package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.RuntimeException")
public class TRuntimeException extends Exception {
    private static final long serialVersionUID = -2639473776752775026L;

    public TRuntimeException() {
        super();
    }

    public TRuntimeException(String message) {
        super(message);
    }

    public TRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TRuntimeException(Throwable cause) {
        super(cause);
    }

    protected TRuntimeException(String message, Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
