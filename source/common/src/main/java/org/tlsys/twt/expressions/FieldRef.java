package org.tlsys.twt.expressions;

import org.tlsys.twt.ClassResolver;
import org.tlsys.twt.TNode;
import org.tlsys.twt.links.ClassVal;
import org.tlsys.twt.links.FieldVal;

public class FieldRef extends TStaticExpression {
    private static final long serialVersionUID = 6123119908995398812L;
    private final TExpression scope;
    private final FieldVal field;

    public FieldRef(TNode parent, TExpression scope, FieldVal field) {
        super(parent);
        this.scope = scope;
        this.field = field;
    }


    public FieldVal getField() {
        return field;
    }

    @Override
    public ClassVal getResult() {
        return ClassResolver.resolve(field.getClassVal()).getField(field.getName()).get().getType();
    }
}
