package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.Optional;
import java.util.function.Predicate;

public class GetValue extends Value implements HavinSourceStart {

    private static final long serialVersionUID = -8067881255608648070L;
    private final SourcePoint point;
    private Value value;

    public GetValue(Value value, SourcePoint point) {
        this.value = value;
        this.point = point;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }

    @Override
    public VClass getType() {
        return value.getType();
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return value.find(name, searchIn);
    }

    @Override
    public void getUsing(Collect c) {
        c.add(value);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(value, replaceControl).ifPresent(e -> value = e);
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
