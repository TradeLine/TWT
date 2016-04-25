package org.tlsys.java.lex;

import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.TAssign;
import org.tlsys.twt.expressions.TExpression;

public class JavaAssign implements TAssign {

    private static final long serialVersionUID = 7760610324802656736L;
    private final TNode parent;
    private TExpression target;
    private TExpression value;

    public JavaAssign(TNode parent) {
        this.parent = parent;
    }

    @Override
    public TExpression getTarget() {
        return target;
    }

    public void setTarget(TExpression target) {
        this.target = target;
    }

    @Override
    public TExpression getValue() {
        return value;
    }

    public void setValue(TExpression value) {
        this.value = value;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
