package org.tlsys.compiler.ast;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class LoopStatement extends Block
{

    public LoopStatement()
    {
        super();
    }

    public LoopStatement(int theBeginIndex)
    {
        super(theBeginIndex);
    }

    public LoopStatement(int theBeginIndex, int theEndIndex)
    {
        super(theBeginIndex, theEndIndex);
    }

    public Expression getExpression()
    {
        return (Expression) getChildAt(1);
    }

    public void setExpression(Expression expression)
    {
        widen(expression);
        setChildAt(1, expression);
    }

    public Block getBlock()
    {
        return (Block) getChildAt(0);
    }

    public void setBlock(Block block)
    {
        widen(block);
        setChildAt(0, block);
    }
}