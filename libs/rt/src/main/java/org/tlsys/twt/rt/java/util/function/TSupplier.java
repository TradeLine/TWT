package org.tlsys.twt.rt.java.util.function;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

@ClassName("java.util.function.Supplier")
@JSClass
@FunctionalInterface
public interface TSupplier<T> {
    T get();
}