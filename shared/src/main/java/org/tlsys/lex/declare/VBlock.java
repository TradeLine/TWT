package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class VBlock extends Operation implements Using, CanUse, Context {
    private static final long serialVersionUID = 7031713493204208024L;
    public final ArrayList<Operation> operations = new ArrayList<>();
    private transient Context parentContext;

    public VBlock(Context parentContext) {
        this.parentContext = parentContext;
    }

    public VBlock() {
    }

    @Override
    public Collect getUsing() {
        Collect c = Collect.create().create();

        for (Operation o : operations)
            c.add(o);
        return c;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        for (Operation o : operations) {
            if (!searchIn.test(o))
                continue;
            Optional<SVar> v = o.find(symbol,searchIn.and(e->e!=this));
            if (v.isPresent())
                return v;
        }
        if (searchIn.test(parentContext))
            return parentContext.find(symbol,searchIn.and(e->e!=this));
        return Optional.empty();
    }
}
