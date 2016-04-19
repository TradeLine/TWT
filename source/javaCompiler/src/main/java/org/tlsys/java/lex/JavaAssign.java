package org.tlsys.java.lex;

import org.tlsys.lex.TAssign;
import org.tlsys.lex.TExpression;
import org.tlsys.lex.TNode;

public class JavaAssign implements TAssign {

    private static final long serialVersionUID = 7760610324802656736L;
    private TExpression target;
    private TExpression value;
    private final TNode parent;

    public JavaAssign(TNode parent) {
        this.parent = parent;
    }

    public void setTarget(TExpression target) {
        this.target = target;
    }

    public void setValue(TExpression value) {
        this.value = value;
    }

    @Override
    public TExpression getTarget() {
        return target;
    }

    @Override
    public TExpression getValue() {
        return value;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
