package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Антон on 16.01.2016.
 */
public class Parens extends Value implements HavinSourceStart {

    private static final long serialVersionUID = -2233739865356402799L;
    private Value value;
    private SourcePoint point;

    public Value getValue() {
        return value;
    }

    public Parens() {
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    public Parens(Value value, SourcePoint point) {
        this.value = Objects.requireNonNull(value);
        this.point = point;
    }

    @Override
    public VClass getType() {
        return value.getType();
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (!searchIn.test(value))
            return Optional.empty();
        return value.find(name, searchIn);
    }

    @Override
    public void getUsing(Collect c) {
        value.getUsing(c);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(value,replaceControl).ifPresent(e->value = e);
    }
}
