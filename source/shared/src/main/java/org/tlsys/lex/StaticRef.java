package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

public class StaticRef extends Value {
    private static final long serialVersionUID = 437232639032642594L;
    private VClass ref;

    public StaticRef() {
    }

    public StaticRef(VClass ref) {
        this.ref = ref;
    }

    @Override
    public VClass getType() {
        return ref;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(ref);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return ref.find(name, searchIn);
    }

    @Override
    public String toString() {
        return "REF:" + getType().getRealName();
    }
}
