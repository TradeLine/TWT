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

    public Context getParentContext() {
        return parentContext;
    }

    public ForLoop() {
    }

    public ForLoop(Context parentContext) {
        this.parentContext = Objects.requireNonNull(parentContext);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        Optional<Context> o = null;
        if (init != null && searchIn.test(init)) {
            o = init.find(name, searchIn);
            if (o.isPresent())
                return o;
        }
        if (value != null && searchIn.test(value)) {
            o = value.find(name, searchIn);
            if (o.isPresent())
                return o;
        }

        if (update != null && searchIn.test(update)) {
            o = init.find(name, searchIn);
            if (o.isPresent())
                return o;
        }
        return parentContext.find(name, searchIn.and(e -> e != this));
    }

    @Override
    public void getUsing(Collect c) {
        c.add(init, value, update, block);
    }
}
