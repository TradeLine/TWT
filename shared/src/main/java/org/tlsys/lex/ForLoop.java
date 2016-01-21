package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VBlock;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ForLoop extends Operation {

    private static final long serialVersionUID = -4797382272772179337L;
    public Operation init;
    public Value value;
    public Operation update;
    public VBlock block;
    private Context parentContext;

    public ForLoop() {
    }

    public ForLoop(Context parentContext) {
        this.parentContext = Objects.requireNonNull(parentContext);
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        Optional<SVar> o = null;
        if (init != null && searchIn.test(init)) {
            o = init.find(symbol, searchIn);
            if (o.isPresent())
                return o;
        }
        if (value != null && searchIn.test(value)) {
            o = value.find(symbol, searchIn);
            if (o.isPresent())
                return o;
        }

        if (update != null && searchIn.test(update)) {
            o = init.find(symbol, searchIn);
            if (o.isPresent())
                return o;
        }
        return parentContext.find(symbol, searchIn.and(e -> e != this));
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(init, value, update, block);
    }
}
