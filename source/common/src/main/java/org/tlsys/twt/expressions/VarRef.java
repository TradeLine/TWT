package org.tlsys.twt.expressions;

import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.members.LocalVar;

public final class VarRef extends TStaticExpression {

    private static final long serialVersionUID = -2662237130880867037L;
    private final LocalVar var;

    public VarRef(TNode parent, LocalVar var) {
        super(parent);
        this.var = var;
    }


    public LocalVar getVar() {
        return var;
    }

    @Override
    public ClassVal getResult() {
        return getVar().getType();
    }
}
