package org.tlsys.lex;

import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class Line extends Operation {

    private static final long serialVersionUID = 6128722248032842560L;
    private final Operation operation;
    private final SourcePoint startPoint;
    private final SourcePoint endPoint;

    private final Operation parent;

    public Line(Operation operation, SourcePoint startPoint, SourcePoint endPoint, Operation parent) {
        this.operation = operation;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.parent = parent;
    }

    public SourcePoint getEndPoint() {
        return endPoint;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return getOperation().find(name, searchIn.and(e -> e != this));
    }

    @Override
    public void getUsing(Collect c) {
        if (getOperation() != null)
            getOperation().getUsing(c);
    }

    public SourcePoint getStartPoint() {
        return startPoint;
    }

    public Operation getOperation() {
        return operation;
    }
}
