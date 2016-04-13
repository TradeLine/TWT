package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class ThisFor extends Value implements HavinSourceStart {

    private static final long serialVersionUID = 5355624188287833307L;
    private final VClass self;
    private final VClass forClazz;
    private SourcePoint point;

    public ThisFor(VClass self, VClass forClazz) {
        this(self, forClazz, null);
    }

    public ThisFor(VClass self, VClass forClazz, SourcePoint point) {
        this.self = self;
        this.forClazz = forClazz;
        this.point = point;
    }

    public VClass getSelf() {
        return self;
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }

    @Override
    public VClass getType() {
        return forClazz;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(self);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "[This " + self + "]";
    }
}