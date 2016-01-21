package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import javafx.scene.layout.VBox;
import org.tlsys.lex.declare.VArgument;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VMethod;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class Lambda extends Value implements Context {
    private static final long serialVersionUID = 3557470342170348577L;
    private VBlock block;
    public ArrayList<VArgument> arguments = new ArrayList<>();

    private VMethod method;
    private Context parentContext;

    public Lambda() {
    }

    public Lambda(VBlock block, VMethod method, Context parentContext) {
        this.block = block;
        this.method = method;
        this.parentContext = parentContext;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        for (VArgument a : arguments)
            if (a.getSymbol() == symbol)
                return Optional.of(a);
        if (!searchIn.test(parentContext))
            return Optional.empty();
        return parentContext.find(symbol, searchIn.and(e->e!=this));
    }

    @Override
    public VClass getType() {
        return method.getParent();
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(arguments.stream().toArray(VArgument[]::new)).add(block);
    }
}
