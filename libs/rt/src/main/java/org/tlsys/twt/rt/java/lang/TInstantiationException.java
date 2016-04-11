package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(InstantiationException.class)
public class TInstantiationException extends ReflectiveOperationException {
    public TInstantiationException() {
        super();
    }
    public TInstantiationException(String s) {
        super(s);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
