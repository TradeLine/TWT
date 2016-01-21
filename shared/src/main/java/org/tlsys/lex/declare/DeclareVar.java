package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.*;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class DeclareVar extends VVar implements Using {
    private static final long serialVersionUID = -8645985351987178557L;

    public DeclareVar(VClass clazz, Symbol.VarSymbol symbol) {
        super(clazz, symbol);
    }

    public Operation init;

    public DeclareVar() {
    }

    @Override
    public Collect getUsing() {
        return super.getUsing().add(init);
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        if (init != null && searchIn.test(init)) {
            Optional<SVar> o = init.find(symbol,searchIn);
            if (o.isPresent())
                return o;
        }
        return super.find(symbol,searchIn);
    }
}
