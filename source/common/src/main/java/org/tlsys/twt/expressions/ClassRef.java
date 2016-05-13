package org.tlsys.twt.expressions;

import org.tlsys.twt.NamedClassVal;
import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;

public final class ClassRef extends TStaticExpression {
    private static final long serialVersionUID = -8753986341015684699L;
    private final ClassVal ref;

    public ClassRef(TNode parent, ClassVal ref) {
        super(parent);
        this.ref = ref;
    }

    public ClassVal getRef() {
        return ref;
    }

    @Override
    public ClassVal getResult() {
        return new NamedClassVal(Class.class);
    }
}
