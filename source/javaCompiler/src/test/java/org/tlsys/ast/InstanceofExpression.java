package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.apache.bcel.generic.Type;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class InstanceofExpression extends Expression
{
    private Expression leftOperand;

    private Type rightOperand;

    @Override
    public void visit(NodeVisiter visitor)
    {
        visitor.visit(this);
    }

    public Expression getLeftOperand()
    {
        return leftOperand;
    }

    public void setLeftOperand(Expression theLeftOperand)
    {
        leftOperand= theLeftOperand;
    }

    public Type getRightOperand()
    {
        return rightOperand;
    }

    public void setRightOperand(Type theRightOperand)
    {
        rightOperand= theRightOperand;
    }
}
