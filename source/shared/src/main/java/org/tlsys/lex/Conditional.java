package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Антон on 16.01.2016.
 */
public class Conditional extends Value {
    private static final long serialVersionUID = 1695282947670619192L;
    private Value value;
    private Value thenValue;
    private Value elseValue;
    private VClass type;

    public Value getValue() {
        return value;
    }

    public Value getThenValue() {
        return thenValue;
    }

    public Value getElseValue() {
        return elseValue;
    }

    public Conditional() {
    }

    public Conditional(Value value, Value thenValue, Value elseValue, VClass type) {
        this.value = value;
        this.thenValue = thenValue;
        this.elseValue = elseValue;
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
        c.add(type).add(value, thenValue, elseValue);
    }
}
