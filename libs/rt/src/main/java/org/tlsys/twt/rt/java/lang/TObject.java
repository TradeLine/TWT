package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.DefaultCast;
import org.tlsys.twt.DefaultGenerator;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.rt.EmptyMethodBody;

@JSClass
@ParentClass(value = "", implement = {})
@ClassName("java.lang.Object")
@ReplaceClass(java.lang.Object.class)
@CodeGenerator(DefaultGenerator.class)
@CastAdapter(DefaultCast.class)
public class TObject {

    private static int hashCodeCounter = 0;

    private int hashCode = ++hashCodeCounter;

    @CodeGenerator(EmptyMethodBody.class)
    //@MethodBodyGen("org.tlsys.twt.rt.EmptyMethodBody")
    public TObject() {
    }

    public static Class getClassOfObject(Object object) {
        if (Script.typeOf(object) == "number") {
            if (Script.code(object, "%1===0"))
                return Integer.class;
            else
                return Float.class;
        }

        if (Script.typeOf(object) == "string")
            return String.class;
        //return Script.code(object,"[",TClass.CLASS_IMP,"]");
        throw new RuntimeException("ERROR!!!");
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    //@InvokeGen("org.tlsys.twt.rt.java.lang.ObjectInvokeAdapter")
    @MethodName("getClass")
    @InvokeGen(ObjectInvokeAdapter.class)
    public Class getJClass() {
        throw new RuntimeException("Not supported");
    }

    public String toString() {
        return getClass().getName()+"@"+hashCode();
    }

    public boolean equals(Object obj) {
        return Script.code(this.hashCode(),"==",obj.hashCode());
    }
}
