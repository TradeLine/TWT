package org.tlsys.lex;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Антон on 16.01.2016.
 */
public class SetField extends Value {
    private static final long serialVersionUID = -6132256584895420566L;
    private Value scope;
    private VField field;
    private Value value;
    private Assign.AsType type;
    private SourcePoint point;

    public SetField() {
    }

    public Value getScope() {
        return scope;
    }

    public VField getField() {
        return field;
    }

    public Value getValue() {
        return value;
    }

    public SetField(Value scope, VField field, Value value, Assign.AsType type, SourcePoint point) {
        this.scope = scope;
        this.field = field;
        this.value = value;
        this.type = type;
        this.point = point;
    }

    public SourcePoint getPoint() {
        return point;
    }

    public Assign.AsType getOpType() {
        return type;
    }

    @Override
    public VClass getType() {
        return value.getType();
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(scope,replaceControl).ifPresent(e->scope = e);
        ReplaceHelper.replace(value,replaceControl).ifPresent(e->value = e);
    }

    @Override
    public void getUsing(Collect c) {
        c.add(scope, field,value);
    }
}
