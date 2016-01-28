package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.JArray;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.lang.reflect.Method")
@ReplaceClass(java.lang.reflect.Method.class)
public class TMethod {
    private String name;
    private String jsName;
    private JArray<Class> arguments;
}
