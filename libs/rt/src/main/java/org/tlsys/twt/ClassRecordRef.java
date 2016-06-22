package org.tlsys.twt;

import org.tlsys.HavinSourceStart;
import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.classes.ClassRecord;

import java.util.Optional;
import java.util.function.Predicate;

public class ClassRecordRef extends Value implements HavinSourceStart {

    private static final long serialVersionUID = -5786770335947660514L;
    private final VClass clazz;
    private final VClass type;
    private final SourcePoint point;

    public ClassRecordRef(VClass clazz, SourcePoint point) {
        this.clazz = clazz;
        this.point = point;

        try {
            type = clazz.getClassLoader().loadClass(ClassRecord.class.getName(), point);
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public VClass getToClass() {
        return clazz;
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(getType()).add(clazz);
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }

}
