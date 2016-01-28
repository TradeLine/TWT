package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.JArray;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.classes.ArgumentRecord;
import org.tlsys.twt.classes.ClassRecord;
import org.tlsys.twt.classes.FieldRecord;
import org.tlsys.twt.classes.MethodRecord;
import org.tlsys.twt.rt.java.lang.reflect.TField;

import java.lang.reflect.Field;

@JSClass
@ClassName("java.lang.Class")
@ReplaceClass(Class.class)
//@CodeGenerator(NativeCodeGenerator.class)
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

    private boolean inited = false;

    public boolean isInited() {
        return inited;
    }

    public void init() {
        //
    }

    private String name;

    private Object cons = null;

    private JArray<TField> fields = new JArray<>();

    public void initFor(ClassRecord cr) {
        this.name = cr.getName();
        String functionBody = "";
        Script.code(cons,".c=",this);

        for (int i = 0; i < cr.getFields().length(); i++) {
            FieldRecord fr = cr.getFields().get(i);
            //TField field = new TField(fr.getName(), fr.getJsName(),CastUtil.cast(this), fr.isStaticFlag());
            //fields.add(field);
            if (fr.isStaticFlag()) {
                Script.code(this,"[",fr.getJsName(),"]=eval(",fr.getInitValue(),")");
            } else {
                functionBody+="this."+fr.getJsName()+"="+fr.getInitValue()+";";
            }
        }

        cons = Script.code("new Function(",functionBody,")");

        for (int i = 0; i < cr.getMethods().length(); i++) {
            MethodRecord mr = cr.getMethods().get(i);
            JArray<String> args = new JArray<>();
            args.add(null);
            for (int j = 0; j < mr.getArguments().length(); j++) {
                ArgumentRecord ar = mr.getArguments().get(j);
                args.add(ar.getName());
            }
            args.add(mr.getBody());
            Object func = Script.code("Function.apply(",args.getJSArray(),")");
            if (mr.isStaticFlag()) {
                Script.code(this,"[",mr.getJsName(),"]=",func);
            } else {
                Script.code(cons,".prototype[",mr.getJsName(),"]=",func);
            }
        }
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
