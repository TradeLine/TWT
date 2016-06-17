package org.tlsys.compiler.ast;

import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class SwitchStatement extends Block {

    private Expression expression;

    public SwitchStatement() {
        super();
    }

    @Override
    public void visit(NodeVisiter visitor) {
        visitor.visit(this);
    }

    public SwitchCase getDefault() {
        return (SwitchCase) getLastChild();
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression theExpression) {
        widen(theExpression);
        expression = theExpression;
    }
}
