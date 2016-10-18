package org.tlsys.twt.desc.builder;


import org.tlsys.twt.desc.FieldDesc;
import org.tlsys.twt.desc.TypeDesc;

public class FieldBuilder<PARENT> extends MemberBuilder<FieldDesc, PARENT, FieldBuilder<PARENT>> {

    private String init;
    private TypeDesc type;
    private String name;
    private final Reciver<PARENT> reciver;

    public FieldBuilder(Reciver<PARENT> reciver) {
        this.reciver = reciver;
    }

    public FieldBuilder() {
        reciver = null;
    }

    public FieldBuilder<PARENT> init(String init) {
        this.init = init;
        return this;
    }

    @Override
    public PARENT build() {
        return reciver.addField(result());
    }

    public FieldBuilder<PARENT> name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public FieldDesc result() {
        return new FieldDesc(name, jsName, staticFlag, init, type);
    }

    public TypeBuilder<FieldBuilder<PARENT>> type() {
        return new TypeBuilder<FieldBuilder<PARENT>>(f->{
            type = f;
            return this;
        });
    }

    public FieldBuilder<PARENT> type(TypeDesc type) {
        this.type = type;
        return this;
    }

    @FunctionalInterface
    public interface Reciver<PARENT> {
        PARENT addField(FieldDesc desc);
    }
}
