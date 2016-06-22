package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;
import org.tlsys.twt.CompileException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class NewArrayLen extends Value {

    private static final long serialVersionUID = -247540962746511193L;
    private ArrayClass clazz;
    public ArrayList<Value> sizes = new ArrayList<>();

    public NewArrayLen() {
    }

    public NewArrayLen(ArrayClass clazz) {
        this.clazz = clazz;
    }

    @Override
    public VClass getType() {
        return clazz;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        for (Value v : sizes) {
            c.add(v);
        }
        c.add(clazz);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        for (int i = 0; i < sizes.size(); i++) {
            Optional<Value> op = ReplaceHelper.replace(sizes.get(i), replaceControl);
            if (op.isPresent())
                sizes.set(i, op.get());
        }
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
