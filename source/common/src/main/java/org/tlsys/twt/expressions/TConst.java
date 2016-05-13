package org.tlsys.twt.expressions;

import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;

public final class TConst extends TExpression {
    private static final long serialVersionUID = 3115522450406478152L;
    private final Object value;
    private final TNode parent;
    private final ClassVal type;

    public TConst(Object value, TNode parent, ClassVal type) {
        this.value = value;
        this.parent = parent;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public TNode getParent() {
        return parent;
    }

    @Override
    public ClassVal getResult() {
        return type;
    }
}
