package org.tlsys.java.lex;

import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.FieldRef;
import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.members.TField;

public class JavaFieldRef implements FieldRef {

    private static final long serialVersionUID = -4446190162293813433L;
    private final TNode parent;
    private final TExpression scope;
    private final TField field;

    public JavaFieldRef(TNode parent, TExpression scope, TField field) {
        this.parent = parent;
        this.scope = scope;
        this.field = field;
    }

    @Override
    public TField getField() {
        return field;
    }

    @Override
    public TExpression getScope() {
        return scope;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
