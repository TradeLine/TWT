package org.tlsys.twt.rt.java.util.concurrent;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;


@ClassName("java.util.concurrent.Future")
@JSClass
public interface TFuture<V> {
    boolean cancel(boolean mayInterruptIfRunning);

    public V get();

    public boolean isCancelled();

    public boolean isDone();
}
