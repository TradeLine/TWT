package org.tlsys.java.lex;

import org.tlsys.lex.StaExpression;
import org.tlsys.lex.TExpression;
import org.tlsys.lex.TNode;

public class JavaStaExpression implements StaExpression {

    private TExpression expression;
    private final TNode parent;

    public JavaStaExpression(TNode parent) {
        this.parent = parent;
    }

    public void setExpression(TExpression expression) {
        this.expression = expression;
    }

    @Override
    public TExpression getExpression() {
        return expression;
    }

    @Override
    public TNode getParent() {
        return parent;
    }
}
