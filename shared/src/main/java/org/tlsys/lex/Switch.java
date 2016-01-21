package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VBlock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class Switch extends Operation {

    private static final long serialVersionUID = -25265118011887293L;
    public final ArrayList<Case> cases = new ArrayList<>();
    private Context parentContext;
    private Value value;

    public Switch(Context parentContext, Value value) {
        this.parentContext = parentContext;
        this.value = value;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        Optional<SVar> o = null;
        if (searchIn.test(value)) {
            o = value.find(symbol, searchIn.and(e -> e == value));
            if (o.isPresent())
                return o;
        }
        for (Case c : cases) {
            if (!searchIn.test(c))
                continue;
            o = c.find(symbol, searchIn);
            if (o.isPresent())
                return o;
        }
        return parentContext.find(symbol, searchIn.and(e -> e != this));
    }

    @Override
    public Collect getUsing() {
        Collect c = Collect.create();
        if (value != null)
            c.add(value);
        for (Case cc : cases)
            c.add(cc);
        return c;
    }

    public static class Case extends Operation {
        private static final long serialVersionUID = 6250212035497367710L;
        public Value value;
        public VBlock block;
        private Switch parent;

        public Case(Switch parent) {
            this.parent = parent;
        }

        @Override
        public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
            return parent.find(symbol, searchIn.and(e -> e != this));
        }

        @Override
        public Collect getUsing() {
            return Collect.create().add(value, block);
        }
    }
}
