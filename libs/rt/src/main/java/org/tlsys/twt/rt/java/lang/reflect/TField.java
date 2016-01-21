package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.InitAliase;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@ClassName("java.lang.reflect.Field")
public class TField {

    private final String name;
    private final String jsName;
    private final Class type;
    private final Class declaringClass;
    private final boolean staticFlag;

    @InitAliase("Create")
    public TField(String name, String jsName, Class type, Class declaringClass, boolean staticFlag) {
        this.name = name;
        this.jsName = jsName;
        this.type = type;
        this.declaringClass = declaringClass;
        this.staticFlag = staticFlag;
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
}
