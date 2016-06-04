package org.tlsys.twt;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class FloatArray {
    private final Object s;

    public FloatArray(float[] data) {
        this.s = Script.code("new Float32Array(", ArrayTools.asJSArray(data),")");
    }

    public FloatArray(int length) {
        this.s = Script.code("new Float32Array(", CastUtil.toObject(length),")");
    }

    public int length() {
        return CastUtil.toInt(Script.code(s,".length"));
    }

    public float get(int index) {
        return CastUtil.toFloat(Script.code(s, "[", CastUtil.toObject(index), "]"));
    }

    public void set(int index, float value) {
        Script.code(s, "[", CastUtil.toObject(index), "]=",CastUtil.toObject(value));
    }
}
