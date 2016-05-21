package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.util.Random;

@ReplaceClass(Random.class)
@JSClass
public class TRandom {
    public long nextLong() {
        return CastUtil.toLong(CastUtil.toObject(Math.floor(Long.MAX_VALUE*(nextFloat()-0.5f))));
    }

    public double nextDouble() {
        return CastUtil.toDouble(CastUtil.toObject(nextFloat()));
    }

    public int nextInt() {
        return CastUtil.toInt(CastUtil.toObject(Math.floor(Integer.MAX_VALUE*(nextFloat()-0.5f))));
    }

    public float nextFloat() {
        return CastUtil.toFloat(Script.code("Math.random()"));
    }
}
