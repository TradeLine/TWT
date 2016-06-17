package org.tlsys.compiler.ast;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ConditionalBranch extends Branch
{

    private Expression expression;

    public ConditionalBranch(int targetIndex)
    {
        super(targetIndex);
    }

    public ConditionalBranch(int theBeginIndex, int theEndIndex, int targetIndex)
    {
        super(targetIndex);
        setExpression(new Expression(theBeginIndex, theEndIndex));
    }

    public Expression getExpression()
    {
        return expression;
    }

    public void setExpression(Expression theExpression)
    {
        expression= theExpression;
        widen(theExpression);
        appendChild(theExpression);
    }

}
