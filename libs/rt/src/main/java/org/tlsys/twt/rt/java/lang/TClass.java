package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;

import java.lang.reflect.Field;

@JSClass
@ClassName("java.lang.Class")
@ReplaceClass(Class.class)
@CodeGenerator(NativeCodeGenerator.class)
public class TClass {
    //@InvokeGen("org.tlsys.twt.rt.java.lang.ClassInvoke")
    public String getName() {
        return Script.code("this.fullName");
    }

    //@InvokeGen("org.tlsys.twt.rt.java.lang.ClassInvoke")
    public String toString() {
        return getName();
    }

    //@InvokeGen("org.tlsys.twt.rt.java.lang.ClassInvoke")
    public String getSimpleName() {
        return Script.code("this.simpleName");
    }

    @JSName("isPrimitive")
    public boolean isPrimitive(){
        return Script.code("this.primitive");
    }

    @JSName("getSuperClass")
    public Class getSuperclass() {
        return Script.code("this.ex");
    }

    @JSName("isArray")
    public boolean isArray() {
        return Script.code("Object.getPrototypeOf(this)==AT");
    }

    private Class arrayClass = null;

    @CodeGenerator(GenArrayClassCreateMethod.class)
    private native void initArrayClass();

    public Class getArrayClass() {
        if (arrayClass == null) {
            initArrayClass();
        }
        return arrayClass;
    }

    public TClass(String name) {
    }

    public Object cast(Object obj) {
        if (obj == null)
            return null;
        if (isInstance(obj))
            return obj;
        throw new ClassCastException("Can not cast from " + getName() + " to " + obj.getClass().getName() );
    }

    public boolean isInstance(Object obj) {
        if (obj == null)
            return false;
        return isAssignableFrom(obj.getClass());
    }

    public boolean isAssignableFrom(Class cls) {
        Class t = CastUtil.cast(this);
        while (cls != null) {
            if (cls == t)
                return true;
            cls = cls.getSuperclass();
        }
        return false;
    }

    public Field getField(String name) {
        for (Field f : getFields()) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public Field[] getFields() {
        return Script.code("this.meta.fields");
    }

    public Class getComponentType() {
        if (!isArray())
            return null;
        return Script.code("getClass(",this,".type,","this.len-1)");
    }
}
