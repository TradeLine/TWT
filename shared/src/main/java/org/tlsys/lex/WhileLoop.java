package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VBlock;

import java.util.Optional;
import java.util.function.Predicate;

public class WhileLoop extends Operation {
    private static final long serialVersionUID = 2948321800645905286L;
    public Value value;

    public VBlock block;
    private Context parentContext;

    public WhileLoop(Context parentContext) {
        this.parentContext = parentContext;
    }

    public WhileLoop() {
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        Optional<SVar> o = null;
        if (value != null && searchIn.test(value)) {
            o = value.find(symbol, searchIn);
            if (o.isPresent())
                return o;
        }
        return parentContext.find(symbol, searchIn.and(e -> e != this));
    }

    @Override
    public void getUsing(Collect c) {
        c.add(value, block);
    }
}
