package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;

import java.util.Optional;
import java.util.function.Predicate;

public class Break extends Operation {

    private static final long serialVersionUID = 4299151552383535159L;
    private Label label;

    public Label getLabel() {
        return label;
    }

    public Break(Label label) {
        this.label = label;
    }

    public Break() {
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
    }
}