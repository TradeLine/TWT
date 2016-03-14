package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

public class ArrayGet extends Value {

    private static final long serialVersionUID = 3118490206552796303L;
    private Value value;
    private Value index;

    public ArrayGet() {
    }

    public Value getValue() {
        return value;
    }

    public Value getIndex() {
        return index;
    }

    public ArrayGet(Value value, Value index) {
        this.value = value;
        this.index = index;
    }

    @Override
    public VClass getType() {
        return ((ArrayClass)value.getType()).getComponent();
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(value, index);
    }
}
