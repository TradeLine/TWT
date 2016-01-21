package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

public class ClassRef extends Value {

    private static final long serialVersionUID = -2999382545639351910L;
    public VClass refTo;

    public ClassRef() {
    }

    @Override
    public VClass getType() {
        return refTo;
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(refTo);
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }
}
