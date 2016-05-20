package org.tlsys.twt.rt.java.lang.ref;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.lang.ref.WeakReference;

@JSClass
@ReplaceClass(WeakReference.class)
public class TWeakReference<T> extends TReference<T> {

    private final Object o = Script.code("new WeakMap()");

    public TWeakReference(T referent) {
        Script.code(o, ".set('V',", referent, ")");
    }

    @Override
    public T get() {
        return CastUtil.cast(Script.code(o, ".get('V')"));
    }

    @Override
    public boolean isEnqueued() {
        return CastUtil.toBoolean(Script.code(o, ".has('V')"));
    }

    @Override
    public boolean enqueue() {
        if (isEnqueued()) {
            Script.code(o, ".delete('V')");
            return true;
        }
        return false;
    }
}
