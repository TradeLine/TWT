package org.tlsys.twt.statement;

import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.TExpression;

public class StaExpression implements TStatement {
    private static final long serialVersionUID = 275417567216928190L;
    private final TNode parent;
    private TExpression expression;

    public StaExpression(TNode parent) {
        this.parent = parent;
    }

    public TExpression getExpression() {
        return expression;
    }

    public void setExpression(TExpression expression) {
        this.expression = expression;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
