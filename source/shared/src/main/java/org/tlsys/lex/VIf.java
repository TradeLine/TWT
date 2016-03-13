package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VBlock;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class VIf extends Operation {
    private static final long serialVersionUID = 6377557826419520191L;
    public Value value;
    public VBlock thenBlock;
    public VBlock elseBlock;
    private Context parentContext;

    public VIf() {
    }

    public VIf(Value value, Context parentContext) {
        this.value = value;
        this.parentContext = Objects.requireNonNull(parentContext);
    }

    public VBlock createThen() {
        thenBlock = new VBlock(this);
        return thenBlock;
    }

    public VBlock createElse() {
        elseBlock = new VBlock(this);
        return elseBlock;
    }

    @Override
    public Optional<SVar> find(String name, Predicate<Context> searchIn) {
        Optional<SVar> v = value.find(name, searchIn);
        if (v.isPresent())
            return v;
        if (searchIn.test(parentContext))
            return parentContext.find(name, e->e!=this);
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(thenBlock).add(elseBlock).add(value);
    }
}
