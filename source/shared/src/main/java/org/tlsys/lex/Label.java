package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.twt.CompileException;

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
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
