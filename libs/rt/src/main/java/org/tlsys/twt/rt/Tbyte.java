package org.tlsys.twt.rt;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.boxcastadapter.ByteAdapter;

@JSClass
@ClassName(value = "byte", primitive = true, nativeName = "B")
@CastAdapter(ByteAdapter.class)
public class Tbyte {

    public static short shortValue(byte value) {
        return CastUtil.toShort(CastUtil.toObject(value));
    }

    public static int intValue(byte value) {
        return CastUtil.toInt(CastUtil.toObject(value));
    }

    public static float floatValue(byte value) {
        return CastUtil.toFloat(CastUtil.toObject(value));
    }

    public static long longValue(byte value) {
        return CastUtil.toLong(CastUtil.toObject(value));
    }

    public static double doubleValue(byte value) {
        return CastUtil.toDouble(CastUtil.toObject(value));
    }


    /*
    @ForceInject
    public static byte intValue(int value) {
        int cut = value & 255;
        return CastUtil.intToByte((cut & 128) > 0 ? -128 + (cut & 127): cut & 127);
    }

    @ForceInject
    public static byte longValue(long value) {
        value += 128;
        int d = CastUtil.toInt(Script.code("Math.floor(",CastUtil.toObject(Math.abs(value) / 256),")"));
        int cof = d * 256;
        //Script.code("Math.floor(",a,")");
        if (value > 0) {
            value = Math.abs(value) - cof;
            return CastUtil.toByte(CastUtil.cast(CastUtil.toObject(value - 128)));
        } else {
            value = value + cof;
            return CastUtil.toByte(CastUtil.cast(CastUtil.toObject(value + 128)));
        }
    }

    @ForceInject
    public static byte shortValue(short value) {
        value += 128;
        int d = CastUtil.toInt(Script.code("Math.floor(",CastUtil.toObject(Math.abs(value) / 256),")"));
        int cof = d * 256;
        //Script.code("Math.floor(",a,")");
        if (value > 0) {
            value = CastUtil.toShort(CastUtil.cast(CastUtil.toObject(Math.abs(value) - cof)));
            return CastUtil.toByte(CastUtil.cast(CastUtil.toObject(value - 128)));
        } else {
            value = CastUtil.toShort(CastUtil.cast(CastUtil.toObject(value + cof)));
            return CastUtil.toByte(CastUtil.cast(CastUtil.toObject(value + 128)));
        }
    }
    */


}
