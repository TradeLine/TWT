package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class InstanceOf extends Value {

    private static final long serialVersionUID = -7698411563310867474L;
    private Value value;
    private VClass clazz;
    private VClass result;

    public InstanceOf() {
    }

    public InstanceOf(Value value, VClass clazz) throws VClassNotFoundException {
        this.value = Objects.requireNonNull(value);
        this.clazz = Objects.requireNonNull(clazz);
        result = clazz.getClassLoader().loadClass(boolean.class.getName());
    }

    @Override
    public VClass getType() {
        return result;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        if (searchIn.test(value))
            return value.find(symbol, searchIn.and(e->e!=this));
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(result, clazz, value);
    }

    public Value getValue() {
        return value;
    }

    public VClass getClazz() {
        return clazz;
    }

    public VClass getResult() {
        return result;
    }
}
