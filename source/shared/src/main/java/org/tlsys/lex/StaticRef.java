package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class StaticRef extends Value implements HavinSourceStart {
    private static final long serialVersionUID = 437232639032642594L;
    private final SourcePoint point;
    private VClass ref;
    private VClass type;

    public StaticRef(VClass ref) {
        this(ref, null);
    }

    public StaticRef(VClass ref, SourcePoint point) {
        this.point = point;
        /*
        try {
            type = ref.getClassLoader().loadClass(Class.class.getName());
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        */
        this.ref = ref;
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }

    @Override
    public VClass getType() {
        return ref;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(ref);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return ref.find(name, searchIn);
    }

    @Override
    public String toString() {
        return "REF:" + getType().getRealName();
    }
}
