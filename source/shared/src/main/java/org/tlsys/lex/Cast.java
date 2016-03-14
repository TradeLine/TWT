package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

public class Cast extends Value {

    private static final long serialVersionUID = -5731266501682976298L;
    private VClass type;
    private Value value;

    public Cast() {
    }

    public Cast(VClass type, Value value) {
        this.type = type;
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(value, type);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (!searchIn.test(value))
            return Optional.empty();
        return value.find(name, searchIn);
    }
}
