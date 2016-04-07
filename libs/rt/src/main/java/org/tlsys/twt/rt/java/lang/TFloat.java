package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(Float.class)
@CastAdapter(BoxingCast.class)
public class TFloat extends Number {
    private static final long serialVersionUID = 6217293767823110454L;
    private final float value;
    public TFloat(float value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return CastUtil.toInt(Script.code("Math.floor(", CastUtil.toObject(value), ")"));
    }

    @Override
    public long longValue() {
        return CastUtil.toLong(Script.code("Math.floor(", CastUtil.toObject(value), ")"));
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return CastUtil.cast(CastUtil.toObject(value));
    }
}
