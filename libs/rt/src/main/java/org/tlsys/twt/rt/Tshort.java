package org.tlsys.twt.rt;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.boxcastadapter.ShortAdapter;
import org.tlsys.twt.rt.java.lang.BoxingCast;

@JSClass
@ClassName(value = "short", primitive = true, nativeName = "S")
@CastAdapter(ShortAdapter.class)
public class Tshort {

    public static byte byteValue(short value) {
        int cut = value & 255;
        return CastUtil.intToByte((cut & 128) > 0 ? -128 + (cut & 127): cut & 127);
    }

    public static int intValue(short value) {
        return CastUtil.toInt(CastUtil.toObject(value));
    }

    public static float floatValue(short value) {
        return CastUtil.toFloat(CastUtil.toObject(value));
    }

    public static long longValue(short value) {
        return CastUtil.toLong(CastUtil.toObject(value));
    }

    public static long doubleValue(short value) {
        return CastUtil.toDouble(CastUtil.toObject(value));
    }
}
