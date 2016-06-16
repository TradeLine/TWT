package org.tlsys.graph;

import org.tlsys.ast.BooleanExpression;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ConditionalEdge extends Edge
{

    private BooleanExpression expression;

    private boolean negate= false;

    ConditionalEdge(Graph graph, Node theSource, Node theTarget)
    {
        super(graph, theSource, theTarget);
    }

    public BooleanExpression getBooleanExpression()
    {
        return expression;
    }

    public void setBooleanExpression(BooleanExpression expr)
    {
        expression= expr;
    }

    public boolean isNegate()
    {
        return negate;
    }

    public void setNegate(boolean theNegate)
    {
        negate= theNegate;
    }
}
