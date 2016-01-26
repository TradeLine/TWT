package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.*;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class DeclareVar extends Operation implements Using {
    private static final long serialVersionUID = -8645985351987178557L;

    private SVar var;

    public DeclareVar(SVar var) {
        this.var = var;
    }

    public SVar getVar() {
        return var;
    }

    public Operation init;

    public DeclareVar() {
    }

    @Override
    public void getUsing(Collect c) {
        c.add(init, var.getType());
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        if (init != null && searchIn.test(init)) {
            Optional<SVar> o = init.find(symbol,searchIn);
            if (o.isPresent())
                return o;
        }
        if (searchIn.test(var) && var.getSymbol()==symbol)
            return Optional.of(var);
        return Optional.empty();
    }
}
