package org.tlsys.lex;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;

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
        return ((ArrayClass) var.getType()).getComponent();
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(var).add(value).add(indexs);
    }

    public Value getVar() {
        return var;
    }

    public Value getIndexs() {
        return indexs;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        ReplaceHelper.replace(value, replaceControl).ifPresent(o -> value = o);
        ReplaceHelper.replace(var, replaceControl).ifPresent(o -> var = o);
        ReplaceHelper.replace(indexs, replaceControl).ifPresent(o -> indexs = o);
    }
}
