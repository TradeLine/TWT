package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import javafx.scene.layout.VBox;
import org.tlsys.lex.declare.DeclareVar;
import org.tlsys.lex.declare.VBlock;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ForEach extends Operation {
    private static final long serialVersionUID = -3334220379606337616L;
    private Value value;
    private DeclareVar item;
    private Context parentContext;
    public VBlock block;

    public ForEach() {
    }

    public ForEach(Value value, DeclareVar item, Context parentContext) {
        this.value = Objects.requireNonNull(value);
        this.item = Objects.requireNonNull(item);
        this.parentContext = Objects.requireNonNull(parentContext);
    }

    public Value getValue() {
        return value;
    }

    public DeclareVar getItem() {
        return item;
    }

    public Context getParentContext() {
        return parentContext;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        if (searchIn.test(item) && item.getSymbol() == symbol)
            return Optional.of(item);
        if (searchIn.test(parentContext))
            return parentContext.find(symbol, searchIn);
        return Optional.empty();
    }

    @Override
    public Collect getUsing() {
        return null;
    }
}
