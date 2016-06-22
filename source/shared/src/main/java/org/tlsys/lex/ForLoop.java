package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class ForLoop extends Operation implements HavinSourceStart {

    private static final long serialVersionUID = -4797382272772179337L;
    public Operation init;
    public Value value;
    public Operation update;
    public VBlock block;
    private Context parentContext;
    private SourcePoint point;

    public ForLoop() {
    }

    public ForLoop(Context parentContext, SourcePoint point) {
        this.parentContext = Objects.requireNonNull(parentContext);
        this.point = point;
    }

    public Context getParentContext() {
        return parentContext;
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        Optional<Context> o = null;
        if (init != null && searchIn.test(init)) {
            o = init.find(name, searchIn);
            if (o.isPresent())
                return o;
        }
        if (value != null && searchIn.test(value)) {
            o = value.find(name, searchIn);
            if (o.isPresent())
                return o;
        }

        if (update != null && searchIn.test(update)) {
            o = init.find(name, searchIn);
            if (o.isPresent())
                return o;
        }
        return parentContext.find(name, searchIn.and(e -> e != this));
    }

    @Override
    public void getUsing(Collect c) {
        c.add(init, value, update, block);
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(init, replaceControl).ifPresent(e->init = e);
        ReplaceHelper.replace(value, replaceControl).ifPresent(e->value = e);
        ReplaceHelper.replace(update, replaceControl).ifPresent(e->update = e);
        ReplaceHelper.replace(block, replaceControl).ifPresent(e->block = e);
    }

    @Override
    public boolean accept(OperationVisiter visiter) throws CompileException {
        return visiter.visit(this);
    }
}
