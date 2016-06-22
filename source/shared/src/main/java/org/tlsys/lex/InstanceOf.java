package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class InstanceOf extends Value implements HavinSourceStart {

    private static final long serialVersionUID = -7698411563310867474L;
    private final SourcePoint point;
    private Value value;
    private VClass clazz;
    private VClass result;

    public InstanceOf(Value value, VClass clazz, SourcePoint point) throws VClassNotFoundException {
        this.value = Objects.requireNonNull(value);
        this.clazz = Objects.requireNonNull(clazz);
        result = clazz.getClassLoader().loadClass(boolean.class.getName(), point);
        this.point = point;
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }

    @Override
    public VClass getType() {
        return result;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (searchIn.test(value))
            return value.find(name, searchIn.and(e->e!=this));
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(result, clazz, value);
    }

    public Value getValue() {
        return value;
    }

    public VClass getClazz() {
        return clazz;
    }

    public VClass getResult() {
        return result;
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(value, replaceControl).ifPresent(e->value = e);
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
