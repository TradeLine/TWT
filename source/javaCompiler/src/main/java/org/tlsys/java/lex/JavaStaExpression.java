package org.tlsys.java.lex;

import org.tlsys.twt.TNode;
import org.tlsys.twt.expressions.TExpression;
import org.tlsys.twt.statement.StaExpression;

public class JavaStaExpression implements StaExpression {

    private final TNode parent;
    private TExpression expression;

    public JavaStaExpression(TNode parent) {
        this.parent = parent;
    }

    @Override
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
