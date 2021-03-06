package org.tlsys.twt;

import org.tlsys.twt.annotations.InvokeGen;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public final class ArrayTools {
    private ArrayTools() {
    }

    public static <T> void add(T[] array, Object element) {
        Script.code(array, ".add(", element, ")");
    }
    public static <T> void remove(T[] array, int index) {
        Script.code(array, ".remove(", CastUtil.toObject(index), ")");
    }

    @InvokeGen(ArrayToolsInvokeGenerator.class)
    public static Object asJSArray(Object array) {
        throw new RuntimeException("Not supported");
    }
}
