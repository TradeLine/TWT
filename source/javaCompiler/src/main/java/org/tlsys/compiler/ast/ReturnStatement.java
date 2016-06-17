package org.tlsys.compiler.ast;

import org.tlsys.compiler.generators.AbstractVisitor;
import org.tlsys.compiler.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ReturnStatement extends ASTNode
{

    private Expression expression;

    public ReturnStatement(int theBeginIndex, int theEndIndex)
    {
        setRange(theBeginIndex, theEndIndex);
    }

    @Override
    public void visit(NodeVisiter visitor)
    {
        visitor.visit(this);
    }

    public void setExpression(Expression theExpression)
    {
        widen(theExpression);
        expression= theExpression;
    }

    public Expression getExpression()
    {
        return expression;
    }
}
