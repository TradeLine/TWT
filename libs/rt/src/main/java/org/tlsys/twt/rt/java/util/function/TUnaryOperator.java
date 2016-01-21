package org.tlsys.twt.rt.java.util.function;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

import java.util.function.Function;


@JSClass
@ClassName("java.util.function.UnaryOperator")
@ReplaceClass(java.util.function.UnaryOperator.class)
public interface TUnaryOperator<T> extends Function<T, T> {

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @param <T> the type of the input and output of the operator
     * @return a unary operator that always returns its input argument
     */
    static <T> TUnaryOperator<T> identity() {
        return t -> t;
    }
}
