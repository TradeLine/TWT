package org.tlsys.lex;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;

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

    public Conditional() {
    }

    public Conditional(Value value, Value thenValue, Value elseValue, VClass type) {
        this.value = value;
        this.thenValue = thenValue;
        this.elseValue = elseValue;
        this.type = type;
    }

    public Value getValue() {
        return value;
    }

    public Value getThenValue() {
        return thenValue;
    }

    public Value getElseValue() {
        return elseValue;
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

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        ReplaceHelper.replace(value, replaceControl).ifPresent(e -> value = e);
        ReplaceHelper.replace(thenValue, replaceControl).ifPresent(e -> thenValue = e);
        ReplaceHelper.replace(elseValue, replaceControl).ifPresent(e -> elseValue = e);
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
