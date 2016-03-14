package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Субочев Антон on 15.01.2016.
 */
public class This extends Value {

    private static final long serialVersionUID = 5355624178287843307L;
    private VClass self;

    public This() {
    }

    public This(VClass self) {
        this.self = self;
    }

    @Override
    public VClass getType() {
        return self;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(self);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }
}
