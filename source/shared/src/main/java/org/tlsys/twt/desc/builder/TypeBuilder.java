package org.tlsys.twt.desc.builder;

import org.tlsys.twt.desc.TypeDesc;

public class TypeBuilder<PARENT> implements Builder<TypeDesc, PARENT> {

    private String type;
    private int array;
    private final TypeReciver<PARENT> reciver;

    public TypeBuilder(TypeReciver reciver) {
        this.reciver = reciver;
    }

    public TypeBuilder() {
        reciver = null;
    }

    public void type(String type) {
        this.type = type;
    }

    public void array(int array) {
        this.array = array;
    }

    @Override
    public PARENT build() {
        return reciver.setType(result());
    }

    @Override
    public TypeDesc result() {
        return new TypeDesc(type, array);
    }

    @FunctionalInterface
    public interface TypeReciver<T> {
        T setType(TypeDesc typeDesc);
    }
}
