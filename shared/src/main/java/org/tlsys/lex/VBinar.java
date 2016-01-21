package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.VClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class VBinar extends Value {
    private static final long serialVersionUID = -3696864346645818468L;
    private Value left;
    private Value right;
    private VClass result;
    private BitType type;

    public VBinar() {
    }

    public VBinar(Value left, Value right, VClass result, BitType type) {
        this.type = type;
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.result = Objects.requireNonNull(result);
    }

    @Override
    public VClass getType() {
        return result;
    }

    @Override
    public Optional<SVar> find(Symbol.VarSymbol symbol, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public Collect getUsing() {
        return Collect.create().add(left,result,result);
    }

    public enum BitType {
        PLUS,
        MINUS,
        EQ,
        OR,
        AND,
        NE,
        LT,
        GE,
        GT,
        LE
    }
}
