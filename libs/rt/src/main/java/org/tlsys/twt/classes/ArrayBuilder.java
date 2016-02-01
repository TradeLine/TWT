package org.tlsys.twt.classes;

import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

import java.util.Objects;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class ArrayBuilder<T> {
    public Class component;
    private T[] array;
    private int level = 0;

    private ArrayBuilder(Class component) {
        this.component = Objects.requireNonNull(component, "Array component is NULL");
    }

    @CodeGenerator(ArrayBuilderBodyGenerator.class)
    public ArrayBuilder len(int ... len) {
        throw new RuntimeException("Must be replace");
    }

    @CodeGenerator(ArrayBuilderBodyGenerator.class)
    public T[] set(T ... values) {
        throw new RuntimeException("Must be replace");
    }

    public T[] get() {
        return array;
    }


}
