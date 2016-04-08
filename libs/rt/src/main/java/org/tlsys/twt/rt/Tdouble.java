package org.tlsys.twt.rt;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.rt.boxcastadapter.DoubleAdapter;

@JSClass
@ClassName(value = "double", primitive = true, nativeName = "D")
@CastAdapter(DoubleAdapter.class)
public class Tdouble {

    public static short shortValue(double value) {
        return (short)intValue(value);
    }

    public static int intValue(double value) {
        return (int)longValue(value);
    }

    public static long longValue(double value) {
        return CastUtil.toLong(CastUtil.toObject(Math.floor(value)));
    }

    public static float floatValue(double value) {
        //TODO добавить приведение размера
        return CastUtil.toFloat(CastUtil.toObject(value));
    }

    public static byte byteValue(double value) {
        return (byte)intValue(value);
    }
}
