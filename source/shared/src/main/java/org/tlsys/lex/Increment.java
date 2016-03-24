package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Optional;
import java.util.function.Predicate;

public class Increment extends Value {

    private static final long serialVersionUID = -2572398552937808883L;
    private Value value;
    private VClass result;
    private IncType type;

    public Increment() {
    }

    public IncType getIncType() {
        return type;
    }

    public Value getValue() {
        return value;
    }

    public Increment(Value value, IncType incType, VClass result) {
        this.value = value;
        this.type = incType;
        this.result = result;
    }

    @Override
    public VClass getType() {
        return result;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(result);
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (!searchIn.test(value))
            return Optional.empty();
        return value.find(name, searchIn);
    }

    public enum IncType {
        PRE_INC,//++X
        POST_INC,//X++
        PRE_DEC,//--X
        POST_DEC,//X--
        NOT,//!X
        NEG//-X
    }
}
