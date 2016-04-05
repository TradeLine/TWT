package org.tlsys.lex;

import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

public class NativeConst extends Value {

    private static final long serialVersionUID = 120112308810566355L;

    private final VClass type;

    public NativeConst(VClass type) {
        this.type = type;
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(getType());
    }
}
