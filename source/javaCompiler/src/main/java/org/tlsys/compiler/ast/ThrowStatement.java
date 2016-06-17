package org.tlsys.compiler.ast;

import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ThrowStatement extends Block {

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public Expression getExpression() {
        return (Expression) getFirstChild();
    }

    public void setExpression(Expression expression) {
        widen(expression);
        appendChild(expression);
    }
}
