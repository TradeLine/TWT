package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class ClassRef extends Value implements HavinSourceStart {

    private static final long serialVersionUID = -2999382545639351910L;
    public final VClass refTo;
    public final VClass type;
    private SourcePoint point;

    public ClassRef(VClass refTo, SourcePoint point) {
        this.refTo = refTo;
        this.point = point;

        try {
            type = refTo.getClassLoader().loadClass(Class.class.getName(), point);
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(refTo, type);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return type.find(name, searchIn);
    }
}
