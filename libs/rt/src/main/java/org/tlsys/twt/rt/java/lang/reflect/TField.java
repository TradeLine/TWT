package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.lang.reflect.Field;

@JSClass
//@ClassName("java.lang.reflect.Field")
@ReplaceClass(Field.class)
//@CodeGenerator(NativeCodeGenerator.class)
public class TField {

    private final String name;
    private final String jsName;
    private Class type;
    private final Class declaringClass;
    private int modificators;

    public TField(String name, String jsName, Class declaringClass, Class type, int modificators) {
        this.name = name;
        this.jsName = jsName;
        this.type = type;
        this.declaringClass = declaringClass;
        this.modificators = modificators;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
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

    public int getModifiers() {
        return modificators;
    }
}
