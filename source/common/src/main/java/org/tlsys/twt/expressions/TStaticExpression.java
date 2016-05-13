package org.tlsys.twt.expressions;

import org.tlsys.twt.TNode;

public abstract class TStaticExpression extends TExpression {

    private static final long serialVersionUID = -2124234781229916114L;
    private final TNode parent;

    public TStaticExpression(TNode parent) {
        this.parent = parent;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
