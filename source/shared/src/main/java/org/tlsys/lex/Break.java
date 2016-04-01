package org.tlsys.lex;

import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class Break extends Operation {

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

    public SourcePoint getPoint() {
        return point;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
    }
}