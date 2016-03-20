package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;

import java.util.Optional;
import java.util.function.Predicate;

public class ClassRef extends Value {

    private static final long serialVersionUID = -2999382545639351910L;
    public final VClass refTo;
    public final VClass type;

    public ClassRef(VClass refTo) {
        this.refTo = refTo;

        try {
            type = refTo.getClassLoader().loadClass(Class.class.getName());
        } catch (VClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VClass getType() {
        return type;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(refTo, type);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return type.find(name, searchIn);
    }
}
