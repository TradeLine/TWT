package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.classes.ClassRecord;
import org.tlsys.twt.rt.java.BoxCast;
import org.tlsys.twt.rt.java.lang.TClass;

import java.util.Objects;

@JSClass
@ClassName("java.lang.reflect.Array")
@ReplaceClass(java.lang.reflect.Array.class)
public final class TArray {
    private TArray() {
    }

    public static Object get(Object array, int index) {
        Object[] ar = CastUtil.cast(array);

        if (array.getClass().getComponentType().isPrimitive()) {
            return BoxCast.toObject(array.getClass().getComponentType(), ar[index]);
        }

        return ar[index];
    }

    public static void set(Object array, int index, Object value){
        if (array.getClass().getComponentType().isPrimitive()) {
            value = BoxCast.toPrimitive(array.getClass().getComponentType(), value);
        }
        Object[] ar = CastUtil.cast(array);
        ar[index] = value;
    }

    public static int getLength(Object array) {
        Object[] ar = CastUtil.cast(array);
        return ar.length;
    }

    @CodeGenerator(ArrayCodeGenerator.class)
    public static Object newInstance(ClassRecord componentType, int length) {
        throw new RuntimeException("Not supported yet!");
    }


    public static Object newInstance(Class<?> componentType, int length) {
        TClass cl = CastUtil.cast(Objects.requireNonNull(componentType, "Component type is NULL"));
        return newInstance(cl.getRecord(), length);
    }

    public static Object newInstance(Class<?> componentType, int... length) {
        //return multiNewArray(componentType, length);
        throw new RuntimeException("Not supported yet!");
    }

}
