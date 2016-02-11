package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.Exception")
public class TException extends Throwable {
    public TException() {
        super();
    }

    public TException(String message) {
        super(message);
    }

    public TException(String message, Throwable cause) {
        super(message, cause);
    }

    public TException(Throwable cause) {
        super(cause);
    }

    protected TException(String message, Throwable cause,
                         boolean enableSuppression,
                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
