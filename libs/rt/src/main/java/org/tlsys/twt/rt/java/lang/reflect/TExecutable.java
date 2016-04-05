package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.classes.MethodRecord;
import org.tlsys.twt.rt.java.lang.TClass;

@JSClass
@ReplaceClass(java.lang.reflect.Executable.class)
public abstract class TExecutable {

    private final TClass parentClass;
    private final MethodRecord record;

    public TExecutable(TClass parentClass, MethodRecord record) {
        this.parentClass = parentClass;
        this.record = record;
    }

    public MethodRecord getRecord() {
        return record;
    }

    public Class<?> getDeclaringClass() {
        return CastUtil.cast(parentClass);
    }
    public abstract String getName();
    //public abstract Class<?>[] getParameterTypes();
    public int getParameterCount() {
        return getRecord().getArguments().length();
    }
}
