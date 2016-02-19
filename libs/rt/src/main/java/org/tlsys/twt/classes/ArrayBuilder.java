package org.tlsys.twt.classes;

import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public final class ArrayBuilder<T> {

    private ArrayBuilder() {
    }

    @InvokeGen(ArrayBuilderBodyGenerator.class)
    @CodeGenerator(ArrayBuilderBodyGenerator.class)
    public static <T> T[] create(Class<T> clazz, T...values){return null;}
}
