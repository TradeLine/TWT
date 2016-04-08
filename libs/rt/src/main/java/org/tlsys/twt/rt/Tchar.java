package org.tlsys.twt.rt;

import org.tlsys.twt.ApplyInvoke;
import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.rt.boxcastadapter.BoxCastAdapter;
import org.tlsys.twt.rt.java.lang.BoxingCast;

@JSClass
@ClassName(value = "char")
@CastAdapter(BoxCastAdapter.class)
public class Tchar {

    /*
    @ForceInject
    public static char toChar(int value) {
        return CastUtil.toChar(Script.code("String.fromCharCode(", CastUtil.toObject(value), ")"));
    }
    */

    public static byte byteValue(char value) {
        return CastUtil.toByte(Script.code(CastUtil.toObject(value), ".charCodeAt(0)"));
    }

    public static int intValue(char value) {
        return byteValue(value);
    }

    public static long longValue(char value) {
        return byteValue(value);
    }

    /*
    @Override
    @InvokeGen(ApplyInvoke.class)
    public String toString() {
        return CastUtil.cast(this);
    }
    */
}
