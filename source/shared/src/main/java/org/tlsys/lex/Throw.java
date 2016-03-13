package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Антон on 16.01.2016.
 */
public class Throw extends Operation {

    private static final long serialVersionUID = -2062082634111339304L;
    private Value value;

    public Value getValue() {
        return value;
    }

    public Throw() {
    }

    public Throw(Value value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<SVar> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        value.getUsing(c);
    }
}
