package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class Return extends Operation implements HavinSourceStart {

    private static final long serialVersionUID = -3739822796450046950L;
    private Value value;
    private SourcePoint point;

    public Return() {
    }

    public Return(Value value, SourcePoint point) {
        this.value = value;
        this.point = point;
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (value == null)
            return Optional.empty();
        if (!searchIn.test(value))
            return Optional.empty();
        return value.find(name,searchIn);
    }

    @Override
    public void getUsing(Collect c) {
        c.add(value);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(value,replaceControl).ifPresent(e->value = e);
    }

    @Override
    public String toString() {
        return "[RETURN " + value + "]";
    }
}
