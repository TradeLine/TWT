package org.tlsys.twt;

import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

import java.lang.reflect.Array;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class JArray<E> {
    private Object o = Script.code("[]");

    public static <T> T[] fromJSArray(Object array, Class<T> clazz) {
        int len = CastUtil.toInt(Script.code(array, ".length"));
        T[] m = CastUtil.cast(Array.newInstance(clazz, len));
        for (int i = 0; i < len; i++) {
            m[i] = Script.code(array, "[", i, "]");
        }

        return m;
    }

    public Object getJSArray() {
        return o;
    }

    public void setJSArray(Object o) {
        this.o = o;
    }

    public void add(E o) {
        Script.code(this.o,".push(",o,")");
    }

    public void add(E o, int index) {
        Script.code(this.o,".splice(",CastUtil.toObject(index),",0,",o,")");
    }

    public boolean remove(E value) {
        int i = indexOf(value);
        if (i >= 0) {
            remove(i);
            return true;
        }
        return false;
    }

    public void remove(int index) {
        Script.code(o,".splice(",CastUtil.toObject(index),",1)");
    }

    public E get(int index) {
        if (index < 0 || index>=length())
            return null;
        return Script.code(o,"[",CastUtil.toObject(index),"]");
    }

    public int length() {
        return CastUtil.toInt(Script.code(o,".length"));
    }

    public int indexOf(E object) {
        for (int i = 0; i < length(); i++) {
            if (get(i) == object)
                return i;
        }
        return -1;
    }

    public boolean contains(E value) {
        return indexOf(value)>=0;
    }

    /*
    public <T> T[] getAsArray(Class<T> clazz) {
        T[] m = CastUtil.cast(Array.newInstance(clazz, length()));
        for (int i = 0; i < length(); i++)
            m[i] = CastUtil.cast(get(i));
        return m;
    }
    */
}
