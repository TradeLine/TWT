package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class SVar extends Value implements HaveSymbol {

    private static final long serialVersionUID = 5407405178456004940L;
    public String name;
    private VClass clazz;
    boolean finalFlag;
    private transient Symbol.VarSymbol symbol;

    public SVar() {
    }

    public SVar(VClass clazz, Symbol.VarSymbol symbol) {
        this.symbol = symbol;
        this.clazz = Objects.requireNonNull(clazz);
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(clazz);
    }

    @Override
    public VClass getType() {
        return clazz;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        if (getSymbol() == symbol)
            return Optional.of(this);
        return Optional.empty();
    }
}
