package org.tlsys.lex;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class VIf extends Operation {
    private static final long serialVersionUID = 6377557826419520191L;
    public Value value;
    public VBlock thenBlock;
    public VBlock elseBlock;
    private Context parentContext;

    public VIf() {
    }

    public VIf(Value value, Context parentContext) {
        this.value = value;
        this.parentContext = Objects.requireNonNull(parentContext);
    }

    public Context getParentContext() {
        return parentContext;
    }

    public VBlock createThen(SourcePoint startPoint, SourcePoint endPoint) {
        thenBlock = new VBlock(this, startPoint, endPoint);
        return thenBlock;
    }

    public VBlock createElse(SourcePoint startPoint, SourcePoint endPoint) {
        elseBlock = new VBlock(this, startPoint, endPoint);
        return elseBlock;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        Optional<Context> v = value.find(name, searchIn);
        if (v.isPresent())
            return v;
        if (searchIn.test(parentContext))
            return parentContext.find(name, e->e!=this);
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(thenBlock).add(elseBlock).add(value);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);

        if (value != null)
            ReplaceHelper.replace(value, replaceControl).ifPresent(e->value = e);

        if (thenBlock != null)
            ReplaceHelper.replace(thenBlock, replaceControl).ifPresent(e->thenBlock = e);

        if (elseBlock != null)
            ReplaceHelper.replace(elseBlock, replaceControl).ifPresent(e->elseBlock = e);
    }
}
