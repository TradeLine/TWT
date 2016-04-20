package org.tlsys.java.lex;

import org.tlsys.lex.FieldRef;
import org.tlsys.lex.TExpression;
import org.tlsys.lex.TNode;
import org.tlsys.lex.members.TField;

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
