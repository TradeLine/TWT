package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VBlock;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class VIf extends Operation {
    private static final long serialVersionUID = 6377557826419520191L;
    public Value value;
    public VBlock thenBlock;
    public VBlock elseBlock;
    private Context parentContext;

    public VIf() {
    }

    public VIf(Value value, Context parentContext) {
        this.value = value;
        this.parentContext = Objects.requireNonNull(parentContext);
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        Optional<SVar> v = value.find(symbol, searchIn);
        if (v.isPresent())
            return v;
        if (searchIn.test(parentContext))
            return parentContext.find(symbol, e->e!=this);
        return Optional.empty();
    }

    @Override
    public Collect getUsing() {
        return value.getUsing().add(thenBlock).add(elseBlock);
    }
}
