package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.Optional;
import java.util.function.Predicate;

public class Break extends Operation implements HavinSourceStart {

    private static final long serialVersionUID = 4299151552383535159L;
    private Label label;
    private SourcePoint point;

    public Break(Label label, SourcePoint point) {
        this.label = label;
        this.point = point;
    }

    public Break() {
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}