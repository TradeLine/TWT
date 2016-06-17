package org.tlsys.compiler.ast;

import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

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