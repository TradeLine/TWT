package org.tlsys.twt;

import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class JDictionary<T> {

    private Object js = Script.code("{}");

    public void set(int key, T value) {
        Script.code(js,"[",CastUtil.toObject(key),"]=",value);
    }

    public void set(String key, T value) {
        Script.code(js,"[",key,"]=",value);
    }

    public T get(int key) {
        Object o = Script.code(js,"[",CastUtil.toObject(key),"]");
        if (Script.isUndefined(o))
            return null;
        return CastUtil.cast(o);
    }

    public T get(String key) {
        Object o = Script.code(js,"[",key,"]");
        if (Script.isUndefined(o))
            return null;
        return CastUtil.cast(o);
    }
}
