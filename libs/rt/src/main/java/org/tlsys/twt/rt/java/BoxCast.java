package org.tlsys.twt.rt.java;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

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

    public static Object toObject(Class clazz, Object object) {

        if (clazz.isPrimitive())
            throw new IllegalArgumentException("Class " + clazz + " is primitive");

        if (clazz == Boolean.class) {
            return new Boolean(CastUtil.toBoolean(object));
        }

        if (clazz == Character.class) {
            return new Character(CastUtil.toChar(object));
        }

        if (clazz == Byte.class) {
            return new Byte(CastUtil.toByte(object));
        }

        if (clazz == Short.class) {
            return new Short(CastUtil.toShort(object));
        }

        if (clazz == Integer.class) {
            return new Integer(CastUtil.toInt(object));
        }

        if (clazz == Long.class) {
            return new Long(CastUtil.toLong(object));
        }

        if (clazz == Float.class) {
            return new Float(CastUtil.toFloat(object));
        }

        if (clazz == Double.class) {
            return new Double(CastUtil.toDouble(object));
        }

        throw new RuntimeException("Can't cast " + object + " to " + clazz.getName());
    }
}
