package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class GetField extends Value implements HavinSourceStart, HavingScope {
    private static final long serialVersionUID = -7101816406223385083L;
    private Value scope;
    private VField field;
    private SourcePoint point;

    public GetField() {
    }

    public GetField(Value scope, VField field) {
        this(scope, field, null);
    }

    public GetField(Value scope, VField field, SourcePoint point) {
        this.scope = scope;
        this.field = field;
        this.point = point;
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    @Override
    public Value getScope() {
        return scope;
    }

    public VField getField() {
        return field;
    }

    @Override
    public VClass getType() {
        return field.getType();
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (searchIn.test(field))
            return field.find(name, searchIn);
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(field);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(scope, replaceControl).ifPresent(e->scope = e);
    }
}
