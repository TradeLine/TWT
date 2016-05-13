package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.classes.MethodRecord;
import org.tlsys.twt.rt.java.lang.TClass;

import java.lang.reflect.InvocationTargetException;

@JSClass
@ClassName("java.lang.reflect.Method")
@ReplaceClass(java.lang.reflect.Method.class)
public class TMethod extends TExecutable {
    public String name;
    public boolean staticFlag = false;

    public TMethod(TClass parentClass, MethodRecord record) {
        super(parentClass, record);
    }


    @Override
    public String getName() {
        return name;
    }

    public Object invoke(Object obj, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        throw new RuntimeException("Not supported yet");
    }
}
