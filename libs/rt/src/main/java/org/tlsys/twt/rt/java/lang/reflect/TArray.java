package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ClassName("java.lang.reflect.Array")
@ReplaceClass(java.lang.reflect.Array.class)
public final class TArray {
    private TArray() {
    }

    public static Object get(Object array, int index) {
        return Script.code(array,".list[",index,"]");
    }

    public static void set(Object array, int index, Object value){
        Script.code(array,".list[",index,"]=",value);
    }

    public static int getLength(Object array) {
        Object[] ar = CastUtil.cast(array);
        return ar.length;
    }

    public static Object newInstance(Class<?> componentType, int length) {
        return multiNewArray(componentType, length);
    }

    public static Object newInstance(Class<?> componentType, int... length) {
        return multiNewArray(componentType, length);
    }

    @InvokeGen(org.tlsys.twt.rt.java.lang.reflect.ArrayInvoke.class)
    private static native Object multiNewArray(Class<?> componentType, int... dimensions);
}
