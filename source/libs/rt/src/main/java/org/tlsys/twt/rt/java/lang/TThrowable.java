package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.Throwable")
public class TThrowable {

    private String detailMessage;

    private Throwable cause = (Throwable) (Object) this;

    public TThrowable() {
    }

    public TThrowable(String message) {
        detailMessage = message;
    }

    public TThrowable(String message, Throwable cause) {
        detailMessage = message;
        this.cause = cause;
    }

    public TThrowable(Throwable cause) {
        detailMessage = (cause == null ? null : cause.toString());
        this.cause = cause;
    }

    protected TThrowable(String message, Throwable cause,
                         boolean enableSuppression,
                         boolean writableStackTrace) {
        detailMessage = message;
        this.cause = cause;
    }

    public String getMessage() {
        return detailMessage;
    }

    public synchronized Throwable getCause() {
        Object o = this;
        return (cause == o ? null : cause);
    }

    public String toString() {
        String s = getClass().getName();
        String message = getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
