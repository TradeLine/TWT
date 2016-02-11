package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VExecute;
import org.tlsys.lex.declare.VMethod;

import java.util.Optional;
import java.util.function.Predicate;

public class FunctionRef extends Value {

    private static final long serialVersionUID = -8246622352244040450L;
    private VExecute replaceMethod;
    private VExecute newMethod;

    public FunctionRef() {
    }

    public FunctionRef(VExecute replaceMethod, VExecute newMethod) {
        this.replaceMethod = replaceMethod;
        this.newMethod = newMethod;
    }

    @Override
    public VClass getType() {
        return replaceMethod.getParent();
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(replaceMethod, newMethod);
    }
}
