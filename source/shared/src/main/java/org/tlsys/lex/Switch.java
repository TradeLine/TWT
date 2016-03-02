package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VBlock;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class Switch extends Operation {

    private static final long serialVersionUID = -25265118011887293L;
    public final ArrayList<Case> cases = new ArrayList<>();
    private Context parentContext;
    private Value value;

    public Value getValue() {
        return value;
    }

    public ArrayList<Case> getCases() {
        return cases;
    }

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
    public void getUsing(Collect c) {
        if (value != null)
            c.add(value);
        for (Case cc : cases)
            c.add(cc);
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
        public void getUsing(Collect c) {
            c.add(value, block);
        }
    }
}
