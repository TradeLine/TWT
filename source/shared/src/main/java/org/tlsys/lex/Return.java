package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;

import java.util.Optional;
import java.util.function.Predicate;

public class Return extends Operation {

    private static final long serialVersionUID = -3739822796450046950L;
    private Value value;

    public Return() {
    }

    public Value getValue() {
        return value;
    }

    public Return(Value value) {
        this.value = value;
    }

    @Override
    public Optional<SVar> find(String name, Predicate<Context> searchIn) {
        if (value == null)
            return Optional.empty();
        if (!searchIn.test(value))
            return Optional.empty();
        return value.find(name,searchIn);
    }

    @Override
    public void getUsing(Collect c) {
        c.add(value);
    }
}
