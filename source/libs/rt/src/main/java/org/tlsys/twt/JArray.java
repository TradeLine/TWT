package org.tlsys.twt;

import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public class JArray<E> {
    private Object o = Script.code("[]");


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
        Script.code(this.o,".splice(",index,",0,",o,")");
    }

    public void remove(int index) {
        Script.code(o,".splice(",index,",1)");
    }

    public E get(int index) {
        if (index < 0 || index>=length())
            return null;
        return Script.code(o,"[",index,"]");
    }

    public int length() {
        return Script.code(o,".length");
    }
}
