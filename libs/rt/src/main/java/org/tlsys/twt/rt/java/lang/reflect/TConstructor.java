package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(java.lang.reflect.Constructor.class)
public class TConstructor<T> extends TExecutable {
    @Override
    public Class<?> getDeclaringClass() {
        return null;
    }

    @Override
    public String getName() {
        return "<init>";
    }
}
