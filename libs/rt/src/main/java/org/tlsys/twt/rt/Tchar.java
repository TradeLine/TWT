package org.tlsys.twt.rt;

import org.tlsys.twt.ApplyInvoke;
import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.*;
import org.tlsys.twt.rt.java.lang.BoxingCast;

@JSClass
@ClassName(value = "char")
@CastAdapter(BoxingCast.class)
public class Tchar {

    @ForceInject
    public static char toChar(int value) {
        return CastUtil.toChar(Script.code("String.fromCharCode(", CastUtil.toObject(value), ")"));
    }

    @ForceInject
    public static int toInt(char value) {
        return CastUtil.toInt(Script.code(CastUtil.toObject(value), ".charCodeAt(0)"));
    }

    @Override
    @InvokeGen(ApplyInvoke.class)
    public String toString() {
        return CastUtil.cast(this);
    }
}
