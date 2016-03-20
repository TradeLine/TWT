package org.tlsys.twt.rt;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.ForceInject;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.BoxingCast;

@JSClass
@ClassName(value = "byte", primitive = true, nativeName = "B")
@CastAdapter(BoxingCast.class)
public class Tbyte {

    @ForceInject
    public static byte fromInt(int value) {

        /*
        int cut = value & 255;
        return CastUtil.intToByte((value > 0) ? ((cut & 128)>0 ? (cut & 127) - 128: cut & 127) : (value & 128)>0 ? (value & 127) - 128 : value & 127);
        */

        int cut = value & 255;
        return CastUtil.intToByte((cut & 128) > 0 ? -128 + (cut & 127): cut & 127);

        /*
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
        */
    }

    @ForceInject
    public static byte fromLong(long value) {
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
    public static byte fromShort(short value) {
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


}
