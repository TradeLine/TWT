package org.tlsys.twt.rt;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.BoxingCast;

@JSClass
@ClassName(value = "byte", primitive = true, nativeName = "B")
@CastAdapter(BoxingCast.class)
public class Tbyte {
    public static byte fromInt(int value) {
        value += 128;
        int d = Script.code("Math.floor(",Math.abs(value) / 256,")");
        int cof = d * 256;
                //Script.code("Math.floor(",a,")");
        if (value > 0) {
            value = Math.abs(value) - cof;
            return CastUtil.cast(value - 128);
        } else {
            value = value + cof;
            return CastUtil.cast(value + 128);
        }
    }

    public static byte fromLong(long value) {
        value += 128;
        int d = Script.code("Math.floor(",Math.abs(value) / 256,")");
        int cof = d * 256;
        //Script.code("Math.floor(",a,")");
        if (value > 0) {
            value = Math.abs(value) - cof;
            return CastUtil.cast(value - 128);
        } else {
            value = value + cof;
            return CastUtil.cast(value + 128);
        }
    }

    public static byte fromShort(short value) {
        value += 128;
        int d = Script.code("Math.floor(",Math.abs(value) / 256,")");
        int cof = d * 256;
        //Script.code("Math.floor(",a,")");
        if (value > 0) {
            value = CastUtil.cast(Math.abs(value) - cof);
            return CastUtil.cast(value - 128);
        } else {
            value = CastUtil.cast(value + cof);
            return CastUtil.cast(value + 128);
        }
    }


}
