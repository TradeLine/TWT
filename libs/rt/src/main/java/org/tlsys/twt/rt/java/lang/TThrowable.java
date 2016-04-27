package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(java.lang.Throwable.class)
public class TThrowable {

    private String detailMessage;

    private Throwable cause = CastUtil.cast(this);

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
        detailMessage = (cause == null || Script.isUndefined(cause) ? null : cause.toString());
        this.cause = cause;
    }

    protected TThrowable(String message, Throwable cause,
                         boolean enableSuppression,
                         boolean writableStackTrace) {
        detailMessage = message;
        this.cause = cause;
    }

    public static Object jsErrorConvert(Object o) {
        if (!Script.isUndefined(Script.code(o, "[", TObject.CLASS_RECORD, "]")))
            return o;
        if (Script.code(o, " instanceof TypeError")) {
            String message = Script.code(o, ".message");
            if (message.endsWith(" not a function")) {
                Object out = new NoSuchMethodException(message);
                Script.code(out,".steck=",o,".steck");
                return out;
            }
            if (message.endsWith(" of null")) {
                Object out = new NullPointerException();
                Script.code(out,".steck=",o,".steck");
                return out;
            }
        }
        String message = Script.code(o, ".toString()");
        Object out = new RuntimeException(message);
        Script.code(out,".steck=",o,".steck");
        return out;
    }

    public String getMessage() {
        return detailMessage;
    }

    public synchronized Throwable getCause() {
        Object o = this;
        if (Script.isUndefined(cause))
            return null;
        return (cause == o ? null : cause);
    }

    public String toString() {
        String out = getClass().getName();
        if (getMessage() != null)
            out = out + ": " + getMessage();
        if (getCause() != null)
            out = out + ": " + getCause();
        return out;
    }
}
