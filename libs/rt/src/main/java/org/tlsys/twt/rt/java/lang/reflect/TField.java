package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.rt.java.lang.NativeCodeGenerator;
import org.tlsys.twt.rt.java.lang.TClassLoader;

import java.lang.reflect.Field;

@JSClass
@ClassName("java.lang.reflect.Field")
@ReplaceClass(Field.class)
@CodeGenerator(NativeCodeGenerator.class)
public class TField {

    private final String name;
    private final String jsName;
    private Class type;
    private final Class declaringClass;
    private TClassLoader.TypeProvider typeProvider;
    private final boolean staticFlag;

    public TField(String name, String jsName, TClassLoader.TypeProvider typeProvider, Class declaringClass, boolean staticFlag) {
        this.name = name;
        this.jsName = jsName;
        this.typeProvider = typeProvider;
        this.type = type;
        this.declaringClass = declaringClass;
        this.staticFlag = staticFlag;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        if (type == null)
            type = typeProvider.getType();
        return type;
    }

    public Class getDeclaringClass() {
        return declaringClass;
    }
    public Object get(Object obj) {
        return Script.code(obj,"[",jsName,"]");
    }

    public void set(Object obj, Object value) {
        Script.code(obj,"[",jsName,"]=",value);
    }
}
