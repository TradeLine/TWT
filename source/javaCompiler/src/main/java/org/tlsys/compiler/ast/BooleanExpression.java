package org.tlsys.compiler.ast;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class BooleanExpression implements Cloneable
{
    private Expression expression;

    public BooleanExpression(Expression newExpression)
    {
        expression= newExpression;
    }

    public Expression getExpression()
    {
        return expression;
    }

}