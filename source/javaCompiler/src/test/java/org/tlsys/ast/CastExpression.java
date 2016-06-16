package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class CastExpression extends Expression {

    private Expression expression;

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public void setExpression(Expression theExpression) {
        widen(theExpression);
        expression = theExpression;
    }

    public Expression getExpression() {
        return expression;
    }
}