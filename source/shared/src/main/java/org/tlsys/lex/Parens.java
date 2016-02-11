package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Антон on 16.01.2016.
 */
public class Parens extends Value {

    private static final long serialVersionUID = -2233739865356402799L;
    private Value value;

    public Value getValue() {
        return value;
    }

    public Parens() {
    }

    public Parens(Value value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public VClass getType() {
        return value.getType();
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        if (!searchIn.test(value))
            return Optional.empty();
        return value.find(symbol, searchIn);
    }

    @Override
    public void getUsing(Collect c) {
        value.getUsing(c);
    }
}
