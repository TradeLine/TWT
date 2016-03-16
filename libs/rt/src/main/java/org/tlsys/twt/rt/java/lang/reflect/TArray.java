package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.classes.ArrayBuilder;

@JSClass
@ClassName("java.lang.reflect.Array")
@ReplaceClass(java.lang.reflect.Array.class)
public final class TArray {
    private TArray() {
    }

    public static Object get(Object array, int index) {
        Object[] ar = CastUtil.cast(array);
        return ar[index];
    }

    public static void set(Object array, int index, Object value){
        Object[] ar = CastUtil.cast(array);
        ar[index] = value;
    }

    public static int getLength(Object array) {
        Object[] ar = CastUtil.cast(array);
        return ar.length;
    }

    @CodeGenerator(ArrayCodeGenerator.class)
    public static Object newInstance(Class<?> componentType, int length) {
        //return multiNewArray(componentType, length);
        throw new RuntimeException("Not supported yet!");
    }

    public static Object newInstance(Class<?> componentType, int... length) {
        //return multiNewArray(componentType, length);
        throw new RuntimeException("Not supported yet!");
    }

}
