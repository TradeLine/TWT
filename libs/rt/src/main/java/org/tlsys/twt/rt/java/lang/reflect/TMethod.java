package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.JArray;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.lang.reflect.Method")
@ReplaceClass(java.lang.reflect.Method.class)
public class TMethod extends TExecutable {
    public String name;
    public boolean staticFlag = false;


    @Override
    public String getName() {
        return name;
    }
/*
    @Override
    public Class<?>[] getParameterTypes() {
        return new Class<?>[0];
    }
    */
}
