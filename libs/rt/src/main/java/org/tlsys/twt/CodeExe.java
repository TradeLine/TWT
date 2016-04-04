package org.tlsys.twt;

import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.lex.Value;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;
import org.tlsys.lex.declare.VExecute;

import java.util.Optional;
import java.util.function.Predicate;

public class CodeExe extends Value {

    private VExecute execute;
    private VClass type;

    public CodeExe(VExecute execute) {
        this.execute = execute;
        try {
            type = execute.getParent().getClassLoader().loadClass(Object.class.getName());
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public Optional<Context> find(String s, Predicate<Context> predicate) {
        if (getExecute().isThis(s) && predicate.test(getExecute()))
            return Optional.of(getExecute());
        return type.find(s, predicate.and(e->e != this));
    }

    public VExecute getExecute() {
        return execute;
    }

    @Override
    public void getUsing(Collect collect) {
        collect.add(getExecute());
    }
}
