package org.tlsys.twt.rt.java.lang.ref;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.lang.ref.Reference;

@JSClass
@ReplaceClass(Reference.class)
public abstract class TReference<T> {
    public abstract T get();

    public abstract boolean isEnqueued();

    public abstract boolean enqueue();
}
