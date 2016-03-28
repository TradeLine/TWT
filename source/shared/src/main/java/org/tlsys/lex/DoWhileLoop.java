package org.tlsys.lex;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VBlock;

import java.util.Optional;
import java.util.function.Predicate;


public class DoWhileLoop extends Operation {
    private static final long serialVersionUID = 208497674134567764L;

    public Value value;

    public VBlock block;
    private Context parentContext;

    public DoWhileLoop(Context parentContext) {
        this.parentContext = parentContext;
    }

    public DoWhileLoop() {
    }

    public Context getParentContext() {
        return parentContext;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        Optional<Context> o = null;
        if (value != null && searchIn.test(value)) {
            o = value.find(name, searchIn);
            if (o.isPresent())
                return o;
        }
        return parentContext.find(name, searchIn.and(e -> e != this));
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
