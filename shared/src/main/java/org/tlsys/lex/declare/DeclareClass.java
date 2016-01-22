package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.lex.SVar;
import org.tlsys.lex.Value;

import java.util.Optional;
import java.util.function.Predicate;

public class DeclareClass extends Value {

    private VClass clazz;

    public DeclareClass(VClass clazz) {
        this.clazz = clazz;
    }

    public DeclareClass() {
    }



    @Override
    public VClass getType() {
        return clazz;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(getType());
    }
}
