package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class WhileLoop extends Operation implements HavinSourceStart {
    private static final long serialVersionUID = 2948321800645905286L;
    public Value value;

    public VBlock block;
    private Context parentContext;
    private SourcePoint point;

    public WhileLoop(Context parentContext, SourcePoint point) {
        this.parentContext = parentContext;
        this.point = point;
    }

    public WhileLoop() {
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
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
