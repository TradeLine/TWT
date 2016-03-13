package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;

import java.util.Optional;
import java.util.function.Predicate;

public interface Context {
    public Optional<SVar> find(String name, Predicate<Context> searchIn);
    public default Optional<Label> findLabel(String name) {
        return Optional.empty();
    }
}
