package org.tlsys.twt.expressions;

import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;

public final class This extends TStaticExpression {
    private static final long serialVersionUID = -5621509118377867951L;
    private final ClassVal clazz;

    public This(TNode parent, ClassVal clazz) {
        super(parent);
        this.clazz = clazz;
    }

    public ClassVal getClassRef() {
        return clazz;
    }

    @Override
    public ClassVal getResult() {
        return getClassRef();
    }
}
