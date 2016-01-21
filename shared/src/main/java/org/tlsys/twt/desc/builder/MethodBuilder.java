package org.tlsys.twt.desc.builder;

import org.tlsys.twt.desc.ArgumentDesc;
import org.tlsys.twt.desc.MethodDesc;
import org.tlsys.twt.desc.TypeDesc;

public class MethodBuilder<PARENT> extends ExeBuilder<MethodDesc, PARENT, MethodBuilder> {

    private String name;
    private TypeDesc result;
    private final Reciver<PARENT> reciver;

    public MethodBuilder(Reciver<PARENT> reciver) {
        this.reciver = reciver;
    }

    public MethodBuilder() {
        reciver = null;
    }

    public MethodBuilder<PARENT> name(String name) {
        this.name = name;
        return this;
    }

    public TypeBuilder<MethodBuilder<PARENT>> resultType() {
        return new TypeBuilder<MethodBuilder<PARENT>>(t->{
            result = t;
            return this;
        });
    }

    public MethodBuilder<PARENT> resultType(TypeDesc type) {
        this.result = type;
        return this;
    }

    @Override
    public PARENT build() {
        return reciver.addMethod(result());
    }

    @Override
    public MethodDesc result() {
        return new MethodDesc(name, jsName, staticFlag, result, arguments.stream().toArray(ArgumentDesc[]::new), body);
    }

    public static interface Reciver<PARENT> {
        public PARENT addMethod(MethodDesc methodDesc);
    }
}
