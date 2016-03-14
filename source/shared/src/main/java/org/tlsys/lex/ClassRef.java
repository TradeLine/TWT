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
    public void getUsing(Collect c) {
        c.add(refTo);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }
}
