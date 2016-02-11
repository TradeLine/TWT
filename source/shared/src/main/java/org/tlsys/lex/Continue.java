package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;

import java.util.Optional;
import java.util.function.Predicate;

public class Continue extends Operation {

    private static final long serialVersionUID = 4299151552383535059L;
    private Label label;

    public Label getLabel() {
        return label;
    }

    public Continue(Label label) {
        this.label = label;
    }

    public Continue() {
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
    }
}
