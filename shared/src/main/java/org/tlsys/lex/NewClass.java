package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VConstructor;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class NewClass extends Value {
    private static final long serialVersionUID = -2655271103608417622L;
    public VConstructor constructor;
    public ArrayList<Value> arguments = new ArrayList<>();

    public NewClass() {
    }

    public NewClass(VConstructor constructor) {
        this.constructor = constructor;
    }

    @Override
    public VClass getType() {
        return constructor.getParent();
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(constructor);
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }
}
