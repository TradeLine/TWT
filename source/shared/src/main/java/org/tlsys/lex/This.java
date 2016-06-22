package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.Optional;
import java.util.function.Predicate;

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
        if (self == null) {
            System.out.println("132");
            throw new RuntimeException(new CompileException("Self type of this is NULL", point));
        }
        this.self = self;
        this.point = point;
    }

    @Override
    public SourcePoint getStartPoint() {
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

    @Override
    public String toString() {
        return "[This " + self + "]";
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
