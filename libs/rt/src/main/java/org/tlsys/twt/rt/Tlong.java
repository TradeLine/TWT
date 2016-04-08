package org.tlsys.twt.rt;

import org.tlsys.twt.ApplyInvoke;
import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.boxcastadapter.LongAdapter;

@JSClass
@ClassName(value = "long", nativeName = "J", primitive = true)
@CastAdapter(LongAdapter.class)
public class Tlong {

    public static byte byteValue(long value) {
        int cut = CastUtil.toInt(CastUtil.toObject(value)) & 255;
        return CastUtil.intToByte((cut & 128) > 0 ? -128 + (cut & 127): cut & 127);
    }

    public static float floatValue(long value) {
        return CastUtil.toFloat(CastUtil.toObject(value));
    }

    public static long doubleValue(long value) {
        return CastUtil.toDouble(CastUtil.toObject(value));
    }

    public static int intValue(long value) {
        //TODO добавить приведение размера
        return CastUtil.toInt(CastUtil.toObject(value));
    }

}
