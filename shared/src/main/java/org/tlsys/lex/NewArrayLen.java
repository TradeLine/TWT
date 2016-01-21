package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class NewArrayLen extends Value {

    private static final long serialVersionUID = -247540962746511193L;
    private ArrayClass clazz;
    public ArrayList<Value> sizes = new ArrayList<>();

    public NewArrayLen() {
    }

    public NewArrayLen(ArrayClass clazz) {
        this.clazz = clazz;
    }

    @Override
    public VClass getType() {
        return clazz;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public Collect getUsing() {
        Collect c = Collect.create();
        for (Value v : sizes) {
            c.add(v);
        }
        c.add(clazz);
        return c;
    }
}
