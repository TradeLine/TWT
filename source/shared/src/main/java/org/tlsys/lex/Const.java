package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.lex.SVar;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

public class Const extends Value {

    private static final long serialVersionUID = -6904428609017177692L;
    private Object value;
    private VClass type;

    public Const() {
    }

    public Const(Object value, VClass type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(type);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }
}
