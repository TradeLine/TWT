package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.lex.declare.VMethod;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Invoke extends Value {
    private static final long serialVersionUID = -2146704597568339297L;
    private VExecute method;
    public ArrayList<Value> arguments = new ArrayList<>();
    private Value self;

    public Invoke() {
    }

    public Invoke(VExecute method, Value self) {
        this.self = self;
        this.method = Objects.requireNonNull(method);
    }

    public Value getSelf() {
        return self;
    }

    public VExecute getMethod() {
        return method;
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(method);
    }

    @Override
    public VClass getType() {
        return method.returnType;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }
}
