package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.java.BoxCast;

import java.lang.reflect.Field;

@JSClass
//@ClassName("java.lang.reflect.Field")
@ReplaceClass(Field.class)
//@CodeGenerator(NativeCodeGenerator.class)
public class TField {

    private final String name;
    private final String jsName;
    private final Class declaringClass;
    private Class type;
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

        /*
        if (Modifier.isStatic(getModifiers())) {
            TClass c = CastUtil.cast(getDeclaringClass());
            c.getRecord().getPrototype()
        }
        */

        if (type.isPrimitive()) {
            Script.code(obj,"[",jsName,"]=",BoxCast.toPrimitive(type, value));
            return;
        }

        Script.code(obj,"[",jsName,"]=",value);
    }

    public int getModifiers() {
        return modificators;
    }
}
