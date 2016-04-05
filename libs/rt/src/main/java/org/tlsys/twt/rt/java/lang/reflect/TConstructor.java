package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.classes.MethodRecord;
import org.tlsys.twt.rt.java.lang.TClass;

@JSClass
@ReplaceClass(java.lang.reflect.Constructor.class)
public class TConstructor<T> extends TExecutable {

    public TConstructor(TClass parentClass, MethodRecord record) {
        super(parentClass, record);
    }

    @Override
    public Class<?> getDeclaringClass() {
        return null;
    }

    @Override
    public String getName() {
        return "<init>";
    }
}
