package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.JArray;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(java.lang.reflect.Executable.class)
public abstract class TExecutable {

    public Class parentClass;
    public String jsName;
    public JArray<Class> arguments = new JArray<>();
    public Object jsFunction;

    public Class<?> getDeclaringClass() {
        return parentClass;
    }
    public abstract String getName();
    //public abstract Class<?>[] getParameterTypes();
    public int getParameterCount() {
        return arguments.length();
    }
}
