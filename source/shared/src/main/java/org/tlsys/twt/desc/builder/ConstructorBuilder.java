package org.tlsys.twt.desc.builder;

import org.tlsys.twt.desc.ArgumentDesc;
import org.tlsys.twt.desc.ConstructorDesc;

import java.util.ArrayList;

/**
 * Created by Субочев Антон on 16.12.2015.
 */
public class ConstructorBuilder<PARENT> extends ExeBuilder<ConstructorDesc, PARENT, ConstructorBuilder> {

    private ArrayList<ArgumentDesc> superArgs = new ArrayList<>();
    private final Reciver<PARENT> reciver;

    public ConstructorBuilder(Reciver<PARENT> reciver) {
        this.reciver = reciver;
    }

    public ConstructorBuilder() {
        reciver = null;
    }

    public ArgumentBuilder<ConstructorBuilder<PARENT>> superArg() {
        return new ArgumentBuilder<ConstructorBuilder<PARENT>>((eeee)->{
            superArgs.add(eeee);
            return this;
        });
    }

    @Override
    public PARENT build() {
        return reciver.addConstructor(result());
    }

    @Override
    public ConstructorDesc result() {
        return new ConstructorDesc(jsName, staticFlag, arguments.stream().toArray(ArgumentDesc[]::new), superArgs.stream().toArray(ArgumentDesc[]::new), body);
    }

    public interface Reciver<PARENT> {
        PARENT addConstructor(ConstructorDesc desc);
    }
}
