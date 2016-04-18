package org.tlsys.twt.rt;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.boxcastadapter.FloatAdapter;

@JSClass
@ClassName(value = "float", primitive = true, nativeName = "F")
@CastAdapter(FloatAdapter.class)
public class Tfloat {

    public static double doubleValue(float value) {
        Object o = CastUtil.toObject(value);
        return CastUtil.toDouble(o);
    }

    public static long longValue(float value) {
        return CastUtil.toLong(CastUtil.toObject(Math.floor(value)));
    }

    public static int intValue(float value) {
        return (int) longValue(value);
    }
}
