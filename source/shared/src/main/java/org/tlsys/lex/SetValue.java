package org.tlsys.lex;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class SetValue extends Value {
    private static final long serialVersionUID = 5692500074947385879L;
    private final SourcePoint point;
    private final SourcePoint operationPoint;
    private final VClass result;
    private final Assign.AsType type;
    private Value value;
    private Value newValue;

    public SetValue(Value value, Value newValue, VClass result, Assign.AsType type, SourcePoint point, SourcePoint operationPoint) {
        this.value = value;
        this.newValue = newValue;
        this.result = result;
        this.type = type;
        this.point = point;
        this.operationPoint = operationPoint;
    }

    public SourcePoint getOperationPoint() {
        return operationPoint;
    }

    public Value getNewValue() {
        return newValue;
    }

    public Assign.AsType getAsType() {
        return type;
    }

    public Value getValue() {
        return value;
    }

    public SourcePoint getPoint() {
        return point;
    }

    @Override
    public VClass getType() {
        return result;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return value.find(name, searchIn);
    }

    @Override
    public void getUsing(Collect c) {
        c.add(value).add(newValue);
        //value.getUsing(c);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(value, replaceControl).ifPresent(e -> value = e);
        ReplaceHelper.replace(newValue, replaceControl).ifPresent(e -> newValue = e);
    }
}
