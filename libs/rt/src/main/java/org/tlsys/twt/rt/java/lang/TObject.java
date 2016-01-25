package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.DefaultGenerator;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.rt.EmptyMethodBody;

@JSClass
@ParentClass(value = "", implement = {})
@ClassName("java.lang.Object")
@MetaFactory("org.tlsys.twt.FullMeta")
@ReplaceClass(java.lang.Object.class)
@CodeGenerator(DefaultGenerator.class)
public class TObject {

    private static int hashCodeCounter = 0;

    private int hashCode = ++hashCodeCounter;

    @CodeGenerator(EmptyMethodBody.class)
    //@MethodBodyGen("org.tlsys.twt.rt.EmptyMethodBody")
    public TObject() {
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    //@InvokeGen("org.tlsys.twt.rt.java.lang.ObjectInvokeAdapter")
    @MethodName("getClass")
    public native Class getJClass();

    public String toString() {
        return getClass().getName()+"@"+hashCode();
    }

    public boolean equals(Object obj) {
        return Script.code(this.hashCode(),"==",obj.hashCode());
    }
}
