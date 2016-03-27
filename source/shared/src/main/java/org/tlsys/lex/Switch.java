package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VBlock;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class Switch extends Operation {

    private static final long serialVersionUID = -25265118011887293L;
    public final ArrayList<Case> cases = new ArrayList<>();
    private Context parentContext;
    private Value value;

    public Context getParentContext() {
        return parentContext;
    }

    public Value getValue() {
        return value;
    }

    public ArrayList<Case> getCases() {
        return cases;
    }

    public Switch(Context parentContext, Value value) {
        this.parentContext = parentContext;
        this.value = value;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        Optional<Context> o = null;
        if (searchIn.test(value)) {
            o = value.find(name, searchIn.and(e -> e == value));
            if (o.isPresent())
                return o;
        }
        for (Case c : cases) {
            if (!searchIn.test(c))
                continue;
            o = c.find(name, searchIn);
            if (o.isPresent())
                return o;
        }
        return parentContext.find(name, searchIn.and(e -> e != this));
    }

    @Override
    public void getUsing(Collect c) {
        if (value != null)
            c.add(value);
        for (Case cc : cases)
            c.add(cc);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(value, replaceControl).ifPresent(e->value = e);
        for (int i = 0; i < cases.size(); i++) {
            Optional<Case> op = ReplaceHelper.replace(cases.get(i), replaceControl);
            if (op.isPresent())
                cases.set(i, op.get());
        }
    }

    public static class Case extends Operation {
        private static final long serialVersionUID = 6250212035497367710L;
        public Value value;
        public VBlock block;
        private Switch parent;

        public Case(Switch parent) {
            this.parent = parent;
        }

        @Override
        public Optional<Context> find(String name, Predicate<Context> searchIn) {
            return parent.find(name, searchIn.and(e -> e != this));
        }

        public Switch getParent() {
            return parent;
        }

        @Override
        public void getUsing(Collect c) {
            c.add(value, block);
        }

        @Override
        public void visit(ReplaceVisiter replaceControl) {
            super.visit(replaceControl);
            ReplaceHelper.replace(value, replaceControl).ifPresent(e->value = e);
            ReplaceHelper.replace(block, replaceControl).ifPresent(e->block = e);
        }
    }
}
