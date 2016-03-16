package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;

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

    public SetField(Value scope, VField field, Value value, Assign.AsType type) {
        this.scope = scope;
        this.field = field;
        this.value = value;
        this.type = type;
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
    public void getUsing(Collect c) {
        c.add(scope, field,value);
    }
}
