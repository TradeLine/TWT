package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Субочев Антон on 15.01.2016.
 */
public class This extends Value implements HavinSourceStart {

    private static final long serialVersionUID = 5355624178287843307L;
    private VClass self;
    private SourcePoint point;

    public This() {
    }

    public This(VClass self) {
        this(self, null);
    }

    public This(VClass self, SourcePoint point) {
        this.self = self;
        this.point = point;
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    @Override
    public VClass getType() {
        return self;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(self);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }
}
