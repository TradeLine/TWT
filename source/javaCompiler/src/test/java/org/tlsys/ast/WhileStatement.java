package org.tlsys.ast;

import org.tlsys.Pass1;
import org.tlsys.generators.AbstractVisitor;
import org.tlsys.generators.NodeVisiter;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class WhileStatement extends LoopStatement
{

    public WhileStatement()
    {
        super();
        Pass1.loopFound= false;
    }

    public WhileStatement(int theBeginIndex)
    {
        super(theBeginIndex);
    }

    public WhileStatement(int theBeginIndex, int theEndIndex)
    {
        super(theBeginIndex, theEndIndex);
    }

    @Override
    public void visit(NodeVisiter visitor)
    {
        visitor.visit(this);
    }
}
