package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class Continue extends Operation implements HavinSourceStart {

    private static final long serialVersionUID = 4299151552383535059L;
    private Label label;
    private SourcePoint point;

    public Continue(Label label, SourcePoint point) {
        this.label = label;
        this.point = point;
    }

    public Continue() {
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
}
