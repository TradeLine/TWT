package org.tlsys.twt.rt;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.boxcastadapter.IntAdapter;

@JSClass
@ClassName(value = "int", primitive = true, nativeName = "I")
@CastAdapter(IntAdapter.class)
public class Tint {

    public static char charValue(int value) {
        return CastUtil.toChar(Script.code("String.fromCharCode(", CastUtil.toObject(value),")"));
    }

    public static byte byteValue(int value) {
        int cut = value & 255;
        return CastUtil.intToByte((cut & 128) > 0 ? -128 + (cut & 127): cut & 127);
    }

    public static float floatValue(int value) {
        return CastUtil.toFloat(CastUtil.toObject(value));
    }

    public static long longValue(int value) {
        return CastUtil.toLong(CastUtil.toObject(value));
    }

    public static short shortValue(int value) {
        //TODO добавить приведение размера
        return CastUtil.toShort(CastUtil.toObject(value));
    }

    public static double doubleValue(int value) {
        return CastUtil.toDouble(CastUtil.toObject(value));
    }
}
