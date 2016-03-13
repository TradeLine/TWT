package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;

import java.util.Optional;
import java.util.function.Predicate;

public class Label extends Operation {

    private static final long serialVersionUID = -1346153413651440527L;
    private String name;

    public Label() {
    }

    public Label(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Optional<SVar> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
    }
}
