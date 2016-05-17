package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class Assign extends Value {
    private static final long serialVersionUID = 7982216108065635693L;
    private SVar var;
    private Value value;
    private VClass result;
    private AsType asType;

    public SVar getVar() {
        return var;
    }

    public Value getValue() {
        return value;
    }

    public VClass getResult() {
        return result;
    }

    public AsType getAsType() {
        return asType;
    }

    public Assign() {
    }

    public Assign(SVar var, Value value, VClass result, AsType asType) {
        this.var = var;
        this.value = value;
        this.result = result;
        this.asType = asType;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(var, value, result);
    }

    @Override
    public VClass getType() {
        return result;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (!searchIn.test(value))
            return Optional.empty();
        return value.find(name,searchIn);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(var, replaceControl).ifPresent(e->var = e);
        ReplaceHelper.replace(value, replaceControl).ifPresent(e->value = e);
    }

    public enum AsType {
        ASSIGN,
        PLUS,
        MINUS,
        MUL,
        DIV
    }
}
