package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.Optional;
import java.util.function.Predicate;

public class Const extends Value implements HavinSourceStart {

    private static final long serialVersionUID = -6904428609017177692L;
    private Object value;
    private VClass type;
    private SourcePoint point;

    public Const() {
    }

    public Const(Object value, VClass type) {
        this(value, type, null);
    }

    public Const(Object value, VClass type, SourcePoint point) {
        this.value = value;
        this.type = type;
        this.point = point;
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
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

    @Override
    public String toString() {
        return "[CONST " + value + "]";
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
