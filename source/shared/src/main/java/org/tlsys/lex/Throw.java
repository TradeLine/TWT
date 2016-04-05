package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Антон on 16.01.2016.
 */
public class Throw extends Operation implements HavinSourceStart {

    private static final long serialVersionUID = -2062082634111339304L;
    private Value value;
    private SourcePoint point;

    public Throw() {
    }

    public Throw(Value value, SourcePoint point) {
        this.value = Objects.requireNonNull(value);
        this.point = point;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        value.getUsing(c);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        ReplaceHelper.replace(value, replaceControl).ifPresent(e->value = e);
    }
}
