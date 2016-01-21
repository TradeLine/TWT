package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ArrayAssign extends Value {

    private static final long serialVersionUID = -2007168713803221190L;
    private Value var;
    private Value indexs;
    private Value value;
    private Assign.AsType type;

    public ArrayAssign() {
    }

    public ArrayAssign(Value var, Value value, Value indexs, Assign.AsType type) {
        this.var = var;
        this.type = type;
        this.value = Objects.requireNonNull(value);
        this.indexs = Objects.requireNonNull(indexs);
    }

    @Override
    public VClass getType() {
        return ((ArrayClass)var.getType()).getComponent();
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(var).add(value).add(indexs);
    }
}
