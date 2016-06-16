package org.tlsys.ast;

import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ArrayAccess extends Expression implements Assignable
{

    @Override
    public void visit(NodeVisiter visitor)
    {
        visitor.visit(this);
    }

    public boolean isSame(Object obj)
    {
        if (!(obj instanceof ArrayAccess))
            return false;
        ArrayAccess other= (ArrayAccess) obj;
        if (getArray() instanceof VariableBinding && other.getArray() instanceof VariableBinding)
        {
            VariableBinding vba= (VariableBinding) getArray();
            VariableBinding vbb= (VariableBinding) other.getArray();
            return vba.getVariableDeclaration() == vbb.getVariableDeclaration();
        }
        return false;
    }

    public Expression getArray()
    {
        return (Expression) getChildAt(0);
    }

    public void setArray(Expression array)
    {
        widen(array);
        setChildAt(0, array);
    }

    public Expression getIndex()
    {
        return (Expression) getChildAt(1);
    }

    public void setIndex(Expression index)
    {
        widen(index);
        setChildAt(1, index);
    }
}
