package org.tlsys.twt.desc.builder;

import org.tlsys.twt.desc.ArgumentDesc;
import org.tlsys.twt.desc.TypeDesc;

public class ArgumentBuilder<PARENT> implements Builder<ArgumentDesc, PARENT> {

    private final ArgumentReciver<PARENT> reciver;
    private String name;
    private String jsName;
    private TypeDesc type;

    public ArgumentBuilder(ArgumentReciver<PARENT> reciver) {
        this.reciver = reciver;
    }

    public ArgumentBuilder() {
        reciver = null;
    }

    @Override
    public PARENT build() {
        ArgumentDesc ad = result();
        return reciver.addArgument(ad);
    }

    @Override
    public ArgumentDesc result() {
        return new ArgumentDesc(name, jsName, type);
    }

    public ArgumentBuilder<PARENT> name(String name) {
        this.name = name;
        return this;
    }

    public ArgumentBuilder<PARENT> jsName(String jsName) {
        this.jsName = jsName;
        return this;
    }

    public ArgumentBuilder<PARENT> type(TypeDesc type) {
        this.type = type;
        return this;
    }

    public TypeBuilder<ArgumentBuilder<PARENT>> type() {
        return new TypeBuilder<ArgumentBuilder<PARENT>>(f->{
            type = f;
            return this;
        });
    }

    @FunctionalInterface
    public interface ArgumentReciver<T> {
        T addArgument(ArgumentDesc argument);
    }

}
