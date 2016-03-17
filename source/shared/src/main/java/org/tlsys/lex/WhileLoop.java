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

    public Context getParentContext() {
        return parentContext;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        Optional<Context> o = null;
        if (value != null && searchIn.test(value)) {
            o = value.find(name, searchIn);
            if (o.isPresent())
                return o;
        }
        return parentContext.find(name, searchIn.and(e -> e != this));
    }

    @Override
    public void getUsing(Collect c) {
        c.add(value, block);
    }
}
