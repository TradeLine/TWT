package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;

import java.util.Optional;
import java.util.function.Predicate;

public class GetField extends Value {
    private static final long serialVersionUID = -7101816406223385083L;
    private Value scope;
    private VField field;

    public GetField() {
    }

    public GetField(Value scope, VField field) {
        this.scope = scope;
        this.field = field;
    }

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
    public Optional<SVar> find(String name, Predicate<Context> searchIn) {
        if (searchIn.test(field))
            return field.find(name, searchIn);
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(field);
    }
}
