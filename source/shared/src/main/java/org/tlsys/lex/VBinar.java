package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class VBinar extends Value implements HavinSourceStart {
    private static final long serialVersionUID = -3696864346645818468L;
    private final SourcePoint point;
    private Value left;
    private Value right;
    private VClass result;
    private BitType type;

    public VBinar(Value left, Value right, VClass result, BitType type, SourcePoint point) {
        this.type = type;
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.result = Objects.requireNonNull(result);
        this.point = point;
    }

    public BitType getBitType() {
        return type;
    }

    public Value getLeft() {
        return left;
    }

    public Value getRight() {
        return right;
    }

    public VClass getResult() {
        return result;
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    @Override
    public VClass getType() {
        return result;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        ReplaceHelper.replace(left, replaceControl).ifPresent(o->left = o);
        ReplaceHelper.replace(right, replaceControl).ifPresent(o->right = o);
    }

    @Override
    public void getUsing(Collect c) {
        c.add(left,result,right);
    }

    public enum BitType {
        PLUS,//+
        MINUS,//-
        MUL,//*
        DIV,// /
        EQ,//==
        OR,//||
        AND,//&&
        NE,//!=
        MOD,//%

        USR,//>>>

        LT,//<
        LE,//<=

        GE,//>=
        GT,//>

        BITAND,//&
        BITOR,//|
        BITXOR//^
    }
}
