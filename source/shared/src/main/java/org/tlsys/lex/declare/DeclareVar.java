package org.tlsys.lex.declare;

import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.*;
import org.tlsys.sourcemap.SourcePoint;

import java.util.Optional;
import java.util.function.Predicate;

public class DeclareVar extends Operation implements Using {
    private static final long serialVersionUID = -8645985351987178557L;
    public Operation init;
    private SVar var;
    private SourcePoint point;

    public DeclareVar(SVar var, SourcePoint point) {
        this.var = var;
        this.point = point;
    }

    public DeclareVar() {
    }

    public SourcePoint getPoint() {
        return point;
    }

    public SVar getVar() {
        return var;
    }

    @Override
    public void getUsing(Collect c) {
        c.add(init, var.getType());
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        if (init != null && searchIn.test(init)) {
            Optional<Context> o = init.find(name,searchIn);
            if (o.isPresent())
                return o;
        }
        if (searchIn.test(var) && (name.equals(var.getRealName()) || name.equals(var.getAliasName())))
            return Optional.of(var);
        return Optional.empty();
    }

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        ReplaceHelper.replace(init, replaceControl).ifPresent(e->init = e);
    }
}
