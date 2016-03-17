package org.tlsys.twt.rt;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.BoxingCast;

@JSClass
@ClassName(value = "byte", primitive = true, nativeName = "B")
@CastAdapter(BoxingCast.class)
public class Tbyte {
    public static byte fromInt(int value) {
        int r = (value % 256) + 256;
        if (r > Byte.MAX_VALUE)
            r-=256;
        return CastUtil.cast(r);
    }

    public static byte fromLong(long value) {
        long r = (value % 256) + 256;
        if (r > Byte.MAX_VALUE)
            r-=256;
        return CastUtil.cast(r);
    }

    public static byte fromShort(short value) {
        int r = (value % 256) + 256;
        if (r > Byte.MAX_VALUE)
            r-=256;
        return CastUtil.cast(r);
    }


}
