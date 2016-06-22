package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Set new value to class field
 * 16.01.2016
 *
 * @author caffeine@gmail.com
 */
public class SetField extends Value implements HavinSourceStart, HavingScope {
    private static final long serialVersionUID = -6132256584895420566L;
    private Value scope;
    private VField field;
    private Value value;
    private Assign.AsType type;
    private SourcePoint point;
    private SourcePoint opPoint;

    public SetField() {
    }

    /**
     * @param scope   this класса, поле которого меняется
     * @param field   поле класса
     * @param value   новое значение
     * @param type    тип присвоения
     * @param point   место в исходниках на переменную, значение которой меняется
     * @param opPoint место в исходниках на знак присвоения
     */
    public SetField(Value scope, VField field, Value value, Assign.AsType type, SourcePoint point, SourcePoint opPoint) {
        this.scope = scope;
        this.field = field;
        this.value = value;
        this.type = type;
        this.point = point;
        this.opPoint = opPoint;
    }

    @Override
    public Value getScope() {
        return scope;
    }

    public VField getField() {
        return field;
    }

    public Value getValue() {
        return value;
    }

    public SourcePoint getOpPoint() {
        return opPoint;
    }

    @Override
    public SourcePoint getStartPoint() {
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
        ReplaceHelper.replace(scope, replaceControl).ifPresent(e -> scope = e);
        ReplaceHelper.replace(value, replaceControl).ifPresent(e -> value = e);
    }

    @Override
    public void getUsing(Collect c) {
        c.add(scope, field, value);
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
