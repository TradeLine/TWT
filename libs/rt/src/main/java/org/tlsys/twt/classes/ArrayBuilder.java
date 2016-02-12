package org.tlsys.twt.classes;

import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;

import java.util.Objects;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public final class ArrayBuilder<T> {
    /*
    public Class component;
    private T[] array;

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
    */

    //public Object[] create(Class arrayClass, int[] len);


    private ArrayBuilder() {
    }

    @InvokeGen(ArrayBuilderBodyGenerator.class)
    @CodeGenerator(ArrayBuilderBodyGenerator.class)
    public static <T> T[] create(Class<T> clazz, T...values){return null;}
}
