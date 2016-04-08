package org.tlsys.twt.rt.java;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

import java.lang.annotation.Native;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public final class BoxCast {
    public static Object toPrimitive(Class clazz, Object object) {

        if (!clazz.isPrimitive())
            throw new IllegalArgumentException("Class " + clazz + " is not primitive");

        if (clazz==boolean.class) {
            return CastUtil.toObject(((Boolean)object).booleanValue());
        }

        if (clazz==char.class) {
            return CastUtil.toObject(((Character)object).charValue());
        }

        if (clazz==byte.class) {
            return CastUtil.toObject(((Byte)object).byteValue());
        }

        if (clazz==short.class) {
            return CastUtil.toObject(((Short)object).shortValue());
        }

        if (clazz==int.class) {
            return CastUtil.toObject(((Integer)object).intValue());
        }

        if (clazz==long.class) {
            return CastUtil.toObject(((Long)object).longValue());
        }

        if (clazz==float.class) {
            return CastUtil.toObject(((Float)object).floatValue());
        }

        if (clazz==double.class) {
            return CastUtil.toObject(((Double)object).doubleValue());
        }

        throw new RuntimeException("Can't cast " + object + " to " + clazz.getName());
    }
}
