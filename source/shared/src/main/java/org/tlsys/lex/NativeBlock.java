package org.tlsys.lex;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class NativeBlock extends Operation {

    private static final long serialVersionUID = -870509186145744817L;
    private final ArrayList<Operation> values = new ArrayList<>();

    public NativeBlock add(Operation op) {
        values.add(op);
        return this;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        for (Operation v : values)
            c.add(v);
    }
}
